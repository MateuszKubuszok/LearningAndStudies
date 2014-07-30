/*
 * Kod interpretera maszyny rejestrowej do projektu z JFTT2012
 *
 * Autor: Maciek Gębala
 * http://mgc.im.pwr.wroc.pl/
 * 2011-04-30
 * CLN
*/

#include<iostream>
#include<fstream>
#include<vector>
#include<map>

#include<stdlib.h>

#include<cln/cln.h>

using namespace std;
using namespace cln;

const int READ = 0;
const int WRITE = 1;
const int SET = 2;
const int LOAD = 3;
const int STORE = 4;
const int ADD = 5;
const int SUB = 6;
const int HALF = 7;
const int JUMP = 8;
const int JZ = 9;
const int JG = 10;
const int JODD = 11;
const int HALT = 12;

int main(int argc, char* argv[])
{
    vector< pair<int,pair<int,int> > > program;
    map<int,cl_I> pam;
    vector<cl_I> stale;
    
    cl_I rej[4];
    int lr;
    
    int st =0, k=0;
    cl_I i;
    int i1, i2, i3;
    string com;
    
    if( argc!=2 )
    {
	cout << "Spos�b u�ycia programu: interpreter kod" << endl;
	return -1;
    }
    
    cout << "Czytanie programu." << endl;
    ifstream plik( argv[1] );
    if( !plik )
    {
	cout << "Błąd: Nie można otworzyć pliku " << argv[1] << endl;
	return -1;
    }
    while( !plik.eof() )
    {
	plik >> com;
	i1 = -1;
	if( com=="READ"  ) { i1 = READ; plik >> i2; i3 = 0;
	                     if(i2<0) { cout << "Błąd: zły adress w instrukcji " << k << endl; return -1; }
			   }
	if( com=="WRITE" ) { i1 = WRITE; plik >> i2; i3 = 0;
	                     if(i2<0) { cout << "Błąd: zły adress w instrukcji " << k << endl; return -1; }
			   }
	if( com=="SET" )   { i1 = SET; plik >> i2; plik >> i; stale.push_back(i); i3 = st; st++;
	                     if(i2<0) { cout << "Błąd: zły adress w instrukcji " << k << endl; return -1; }
	                     if(i3<0) { cout << "Błąd: zła stała w instrukcji " << k << endl; return -1; }
			   }

	if( com=="LOAD"  ) { i1 = LOAD; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
	                     if(i3<0) { cout << "Błąd: zły adress w instrukcji " << k << endl; return -1; }
                           }
	if( com=="STORE" ) { i1 = STORE; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
	                     if(i3<0) { cout << "Błąd: zły adress w instrukcji " << k << endl; return -1; }
			   }
	
	if( com=="ADD"   ) { i1 = ADD; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
	                     if((i3<0)||(i3>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	if( com=="SUB"   ) { i1 = SUB; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
	                     if((i3<0)||(i3>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	if( com=="HALF" )  { i1 = HALF; plik >> i2; i3 = 0;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	
	if( com=="JUMP" )  { i1 = JUMP; plik >> i2; i3 = 0; }
	if( com=="JZ" )    { i1 = JZ; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	if( com=="JG" )    { i1 = JG; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	if( com=="JODD" )  { i1 = JODD; plik >> i2; plik >> i3;
	                     if((i2<0)||(i2>3)) { cout << "Błąd: zły rejestr w instrukcji " << k << endl; return -1; }
                           }
	if( com=="HALT" )  { i1 = HALT; i2 = 0; i3 = 0; }
	if( i1==-1 )
	{
	    cout << "Błąd: Nieznana instrukcja w linii " << program.size()+1 << "." << endl;
	    return -1;
	}
	
	if( plik.good() )
	{
	    pair<int,int> temp1(i2,i3);
	    pair<int,pair<int,int> > temp(i1,temp1);
	    program.push_back( temp );
	}
	k++;
    }
    plik.close();
    cout << "Skończono czytanie programu (linii: " << program.size() << ")." << endl;
    
    cout << "Uruchamianie programu." << endl;
    lr = 0;
    for( int j=0; j<4; j++ ) rej[j] = rand();
    i = 0;
    while( program[lr].first!=HALT )	// HALT
    {
	switch( program[lr].first )
	{
	case READ:	cout << "? "; cin >> pam[program[lr].second.first]; lr++; i+=100; break;	// READ
	case WRITE:	cout << "> " << pam[program[lr].second.first] << endl; lr++; i+=100; break;	// WRITE
	case SET:	pam[program[lr].second.first]=stale[program[lr].second.second]; lr++; i+=100; break;	// SET
	
	case LOAD:	rej[program[lr].second.first]=pam[program[lr].second.second]; lr++; i+=10; break;	// LOAD
	case STORE:	pam[program[lr].second.second]=rej[program[lr].second.first]; lr++; i+=10; break;	// STORE
	
	case ADD:	rej[program[lr].second.first]+=rej[program[lr].second.second]; lr++; i++; break;	// ADD
	case SUB:	rej[program[lr].second.first]-=rej[program[lr].second.second];
	                if( rej[program[lr].second.first]<0 ) rej[program[lr].second.first]=0; lr++; i++; break;	// SUB
	case HALF:	rej[program[lr].second.first]=floor1(rej[program[lr].second.first]/(cl_I)2); lr++; i++; break;	// HALF

	case JUMP: 	lr = program[lr].second.first; i++; break;		// JUMP
	case JZ:	if( rej[program[lr].second.first]==0 ) lr = program[lr].second.second; else lr++; i++; break; // JZ
	case JG:	if( rej[program[lr].second.first]>0 ) lr = program[lr].second.second; else lr++; i++; break; // JG
	case JODD:	if( mod(rej[program[lr].second.first],2) == 1 ) lr = program[lr].second.second; else lr++; i++; break; // JODD
	}
	if( lr<0 || lr>=(int)program.size() )
	{
	    cout << "Błąd: Wywołanie nieistniejącej instrukcji nr " << lr << "." << endl;
	    return -1;
	}
	
    }
    cout << "Skończono program (wykonano kroków: " << i << ")." << endl;
    
    return 0;
}
