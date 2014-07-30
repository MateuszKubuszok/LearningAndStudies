function [x, Rho] = eliminacja_gaussa (A, b)
	[M,N] = size (A);
	max_A= rho (A, 1);
	 
	k = 1;
	Rho = max (max (abs (A))) / max_A;
	 
	% eliminacja Gaussa
	for j=2:N
		for i=j:N
			m = A (i, j-1)/A (j-1, j-1); % wspó³czynnik przez jaki mno¿ymy wiersz
			A (i, :) = A (i, :) - A(j-1, :)*m; % odejmuje od i-tego wiersza, ten sam wiersz pomno¿ony przez m
			b (i) = b(i) - m*b(j-1); % powtarza tê operacjê dla wektora b
			
			k = k+1; % któr¹ operacjê na macierzy wykonujemy
			Rho = max ( (max (max (abs (A))) / max_A), Rho); % oblicza wspó³cznynnik wzrostu Rho dla k-tej operacji
		end
	end
	
	% oblicza wynik
	x = zeros (N, 1); % tworzy wektor x-ów
	x (N) = b (N)/A (N,N); % oblicza ostatni¹ wartoœæ wektora x-ów
	
	for j = N-1:-1:1 % od przedostatniego x-a w górê
		x (j) = (b (j)-A (j,j+1:N)*x (j+1:N))/A (j,j); % oblicza kolejne wartoœci wektora x-ów
	end
 end