#include <bitset>
#include <iostream>
using namespace std;

typedef unsigned int               Address;
typedef bitset<8*sizeof (Address)> AddressBin;

AddressBin binary(
	Address address
) {
	return AddressBin(address);
}

void fixLastBit(
	Address& current,
	Address& target
) {
	Address difference = current ^ target;
	current ^= difference ^ (difference & (difference-1));
}

void findBitFixingRouting(
	Address& start,
	Address& target
) {
	int steps = 0;
	cout << "Start:\t" << binary(start) << endl;

	Address current = start;
	while (current != target) {
		fixLastBit(current, target);
		steps++;
		cout << "Fixed:\t" << binary(current) << endl;
	}

	cout << "Target:\t" << binary(target) << endl;
	cout << steps << " steps made" << endl;
}

int main() {
	Address start  = 0;
	Address target = 0;

	cout << "Enter starting point (decimal):" << endl;
	cin >> start;
	cout << "Enter target point (decimal):" << endl;
	cin >> target;

	findBitFixingRouting(start, target);

	return 0;
}