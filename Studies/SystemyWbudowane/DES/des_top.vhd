
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity des_top is
port (

		key_in:	in 	std_logic_vector(0 to 63);  -- wprowadzamy nasz 64o bitowy klucz
		data_in:			in 	std_logic_vector(0 to 63); -- 64 bitowy blok wiadomosci
		function_select: in std_logic; -- 1 gdy szyfrujemy, 0 gdy deszyfrujemy
		data_out:		out 	std_logic_vector(0 to 63); -- wyjscie z zaszyfrowana informacja
		

		des_out_rdy: out std_logic						-- wiadomosc gotowa (nie dziala)

		);
end des_top;

architecture Behavioral of des_top is


component block_top is
port(
		L_in: in std_logic_vector(0 to 31);				-- lewa czesc wiadomosci
		R_in: in std_logic_vector(0 to 31);				-- prawa czesc wiadomosci
	
		L_out: out std_logic_vector(0 to 31);			-- lewe wyjscie
		R_out: out std_logic_vector(0 to 31);			-- prawe wyjscie

		round_key_des: in std_logic_vector(0 to 47)	-- klucz danej rundy

	);
end component;

-- Wewnêtrzne sygna³y DESa
signal L_in_internal, R_in_internal: 	std_logic_vector(0 to 31); -- wejscie do pierwszego bloku spermutowanej wiadomosci
signal L1, R1 : std_logic_vector(0 to 31); -- sygnaly aby polaczyc kolejne bloki rundy
signal L2, R2 : std_logic_vector(0 to 31);
signal L3, R3 : std_logic_vector(0 to 31);
signal L4, R4 : std_logic_vector(0 to 31);
signal L5, R5 : std_logic_vector(0 to 31);
signal L6, R6 : std_logic_vector(0 to 31);
signal L7, R7 : std_logic_vector(0 to 31);
signal L8, R8 : std_logic_vector(0 to 31);
signal L9, R9 : std_logic_vector(0 to 31);
signal L10, R10 : std_logic_vector(0 to 31);
signal L11, R11 : std_logic_vector(0 to 31);
signal L12, R12 : std_logic_vector(0 to 31);
signal L13, R13 : std_logic_vector(0 to 31);
signal L14, R14 : std_logic_vector(0 to 31);
signal L15, R15 : std_logic_vector(0 to 31);
signal L16, R16 : std_logic_vector(0 to 31);
signal L_out_internal, R_out_internal: std_logic_vector(0 to 31); -- wyjscie ktore bedziemy permutowac jeszcze
signal K16, K1:  std_logic_vector(0 to 47);
signal K2,  K3:  std_logic_vector(0 to 47);
signal K4,  K5:  std_logic_vector(0 to 47);
signal K6,  K7:  std_logic_vector(0 to 47);
signal K8,  K9:  std_logic_vector(0 to 47);
signal K10, K11: std_logic_vector(0 to 47);
signal K12, K13: std_logic_vector(0 to 47);
signal K14, K15: std_logic_vector(0 to 47);

begin



WITH function_select SELECT -- wrzucamy klucze w zaleznosci od trybu pracy (przy deszyfrowaniu nalezy odwrocic kolejnosc kluczy tylko, fajna sprawa)
-- permutacje kluczy policzone wczesniej w JAVA'ie 

		K1 <= key_in(9) & key_in(50) & key_in(33) & key_in(59) & key_in(48) & key_in(16) & key_in(32) & key_in(56) & 
				key_in(1) & key_in(8) & key_in(18) & key_in(41) & key_in(2) & key_in(34) & key_in(25) & key_in(24) & 
				key_in(43) & key_in(57) & key_in(58) & key_in(0) & key_in(35) & key_in(26) & key_in(17) & key_in(40) & 
				key_in(21) & key_in(27) & key_in(38) & key_in(53) & key_in(36) & key_in(3) & key_in(46) & key_in(29) & 
				key_in(4) & key_in(52) & key_in(22) & key_in(28) & key_in(60) & key_in(20) & key_in(37) & key_in(62) & 
				key_in(14) & key_in(19) & key_in(44) & key_in(13) & key_in(12) & key_in(61) & key_in(54) & key_in(30)		
				WHEN '1',
				key_in(17) & key_in(58) & key_in(41) & key_in(2) & key_in(56) & key_in(24) & key_in(40) & key_in(35) & 
				key_in(9) & key_in(16) & key_in(26) & key_in(49) & key_in(10) & key_in(42) & key_in(33) & key_in(32) & 
				key_in(51) & key_in(0) & key_in(1) & key_in(8) & key_in(43) & key_in(34) & key_in(25) & key_in(48) & 
				key_in(29) & key_in(4) & key_in(46) & key_in(61) & key_in(44) & key_in(11) & key_in(54) & key_in(37) & 
				key_in(12) & key_in(60) & key_in(30) & key_in(36) & key_in(5) & key_in(28) & key_in(45) & key_in(3) & 
				key_in(22) & key_in(27) & key_in(52) & key_in(21) & key_in(20) & key_in(6) & key_in(62) & key_in(38)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
				
				
WITH function_select SELECT

		K2 <= key_in(1) & key_in(42) & key_in(25) & key_in(51) & key_in(40) & key_in(8) & key_in(24) & key_in(48) & 
				key_in(58) & key_in(0) & key_in(10) & key_in(33) & key_in(59) & key_in(26) & key_in(17) & key_in(16) & 
				key_in(35) & key_in(49) & key_in(50) & key_in(57) & key_in(56) & key_in(18) & key_in(9) & key_in(32) & 
				key_in(13) & key_in(19) & key_in(30) & key_in(45) & key_in(28) & key_in(62) & key_in(38) & key_in(21) & 
				key_in(27) & key_in(44) & key_in(14) & key_in(20) & key_in(52) & key_in(12) & key_in(29) & key_in(54) & 
				key_in(6) & key_in(11) & key_in(36) & key_in(5) & key_in(4) & key_in(53) & key_in(46) & key_in(22)
				WHEN '1',
				key_in(25) & key_in(1) & key_in(49) & key_in(10) & key_in(35) & key_in(32) & key_in(48) & key_in(43) & 
				key_in(17) & key_in(24) & key_in(34) & key_in(57) & key_in(18) & key_in(50) & key_in(41) & key_in(40) & 
				key_in(59) & key_in(8) & key_in(9) & key_in(16) & key_in(51) & key_in(42) & key_in(33) & key_in(56) & 
				key_in(37) & key_in(12) & key_in(54) & key_in(6) & key_in(52) & key_in(19) & key_in(62) & key_in(45) & 
				key_in(20) & key_in(5) & key_in(38) & key_in(44) & key_in(13) & key_in(36) & key_in(53) & key_in(11) & 
				key_in(30) & key_in(4) & key_in(60) & key_in(29) & key_in(28) & key_in(14) & key_in(3) & key_in(46)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
				
WITH function_select SELECT				

		K3 <= key_in(50) & key_in(26) & key_in(9) & key_in(35) & key_in(24) & key_in(57) & key_in(8) & key_in(32) & 
				key_in(42) & key_in(49) & key_in(59) & key_in(17) & key_in(43) & key_in(10) & key_in(1) & key_in(0) & 
				key_in(48) & key_in(33) & key_in(34) & key_in(41) & key_in(40) & key_in(2) & key_in(58) & key_in(16) & 
				key_in(60) & key_in(3) & key_in(14) & key_in(29) & key_in(12) & key_in(46) & key_in(22) & key_in(5) & 
				key_in(11) & key_in(28) & key_in(61) & key_in(4) & key_in(36) & key_in(27) & key_in(13) & key_in(38) & 
				key_in(53) & key_in(62) & key_in(20) & key_in(52) & key_in(19) & key_in(37) & key_in(30) & key_in(6)
				WHEN '1',
				key_in(41) & key_in(17) & key_in(0) & key_in(26) & key_in(51) & key_in(48) & key_in(35) & key_in(59) & 
				key_in(33) & key_in(40) & key_in(50) & key_in(8) & key_in(34) & key_in(1) & key_in(57) & key_in(56) & 
				key_in(10) & key_in(24) & key_in(25) & key_in(32) & key_in(2) & key_in(58) & key_in(49) & key_in(43) & 
				key_in(53) & key_in(28) & key_in(3) & key_in(22) & key_in(5) & key_in(4) & key_in(11) & key_in(61) & 
				key_in(36) & key_in(21) & key_in(54) & key_in(60) & key_in(29) & key_in(52) & key_in(6) & key_in(27) & 
				key_in(46) & key_in(20) & key_in(13) & key_in(45) & key_in(44) & key_in(30) & key_in(19) & key_in(62)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
				
WITH function_select SELECT
		
		K4 <= key_in(34) & key_in(10) & key_in(58) & key_in(48) & key_in(8) & key_in(41) & key_in(57) & key_in(16) & 
				key_in(26) & key_in(33) & key_in(43) & key_in(1) & key_in(56) & key_in(59) & key_in(50) & key_in(49) & 
				key_in(32) & key_in(17) & key_in(18) & key_in(25) & key_in(24) & key_in(51) & key_in(42) & key_in(0) & 
				key_in(44) & key_in(54) & key_in(61) & key_in(13) & key_in(27) & key_in(30) & key_in(6) & key_in(52) & 
				key_in(62) & key_in(12) & key_in(45) & key_in(19) & key_in(20) & key_in(11) & key_in(60) & key_in(22) & 
				key_in(37) & key_in(46) & key_in(4) & key_in(36) & key_in(3) & key_in(21) & key_in(14) & key_in(53)
				WHEN '1',
				key_in(57) & key_in(33) & key_in(16) & key_in(42) & key_in(2) & key_in(35) & key_in(51) & key_in(10) & 
				key_in(49) & key_in(56) & key_in(1) & key_in(24) & key_in(50) & key_in(17) & key_in(8) & key_in(43) & 
				key_in(26) & key_in(40) & key_in(41) & key_in(48) & key_in(18) & key_in(9) & key_in(0) & key_in(59) & 
				key_in(6) & key_in(44) & key_in(19) & key_in(38) & key_in(21) & key_in(20) & key_in(27) & key_in(14) & 
				key_in(52) & key_in(37) & key_in(3) & key_in(13) & key_in(45) & key_in(5) & key_in(22) & key_in(12) & 
				key_in(62) & key_in(36) & key_in(29) & key_in(61) & key_in(60) & key_in(46) & key_in(4) & key_in(11)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K5 <= key_in(18) & key_in(59) & key_in(42) & key_in(32) & key_in(57) & key_in(25) & key_in(41) & key_in(0) & 
				key_in(10) & key_in(17) & key_in(56) & key_in(50) & key_in(40) & key_in(43) & key_in(34) & key_in(33) & 
				key_in(16) & key_in(1) & key_in(2) & key_in(9) & key_in(8) & key_in(35) & key_in(26) & key_in(49) & 
				key_in(28) & key_in(38) & key_in(45) & key_in(60) & key_in(11) & key_in(14) & key_in(53) & key_in(36) & 
				key_in(46) & key_in(27) & key_in(29) & key_in(3) & key_in(4) & key_in(62) & key_in(44) & key_in(6) & 
				key_in(21) & key_in(30) & key_in(19) & key_in(20) & key_in(54) & key_in(5) & key_in(61) & key_in(37)
				WHEN '1',
				key_in(8) & key_in(49) & key_in(32) & key_in(58) & key_in(18) & key_in(51) & key_in(2) & key_in(26) & 
				key_in(0) & key_in(43) & key_in(17) & key_in(40) & key_in(1) & key_in(33) & key_in(24) & key_in(59) & 
				key_in(42) & key_in(56) & key_in(57) & key_in(35) & key_in(34) & key_in(25) & key_in(16) & key_in(10) & 
				key_in(22) & key_in(60) & key_in(4) & key_in(54) & key_in(37) & key_in(36) & key_in(12) & key_in(30) & 
				key_in(5) & key_in(53) & key_in(19) & key_in(29) & key_in(61) & key_in(21) & key_in(38) & key_in(28) & 
				key_in(11) & key_in(52) & key_in(45) & key_in(14) & key_in(13) & key_in(62) & key_in(20) & key_in(27)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K6 <= key_in(2) & key_in(43) & key_in(26) & key_in(16) & key_in(41) & key_in(9) & key_in(25) & key_in(49) & 
				key_in(59) & key_in(1) & key_in(40) & key_in(34) & key_in(24) & key_in(56) & key_in(18) & key_in(17) & 
				key_in(0) & key_in(50) & key_in(51) & key_in(58) & key_in(57) & key_in(48) & key_in(10) & key_in(33) & 
				key_in(12) & key_in(22) & key_in(29) & key_in(44) & key_in(62) & key_in(61) & key_in(37) & key_in(20) & 
				key_in(30) & key_in(11) & key_in(13) & key_in(54) & key_in(19) & key_in(46) & key_in(28) & key_in(53) & 
				key_in(5) & key_in(14) & key_in(3) & key_in(4) & key_in(38) & key_in(52) & key_in(45) & key_in(21)
				WHEN '1',
				key_in(24) & key_in(0) & key_in(48) & key_in(9) & key_in(34) & key_in(2) & key_in(18) & key_in(42) & 
				key_in(16) & key_in(59) & key_in(33) & key_in(56) & key_in(17) & key_in(49) & key_in(40) & key_in(10) & 
				key_in(58) & key_in(43) & key_in(8) & key_in(51) & key_in(50) & key_in(41) & key_in(32) & key_in(26) & 
				key_in(38) & key_in(13) & key_in(20) & key_in(3) & key_in(53) & key_in(52) & key_in(28) & key_in(46) & 
				key_in(21) & key_in(6) & key_in(4) & key_in(45) & key_in(14) & key_in(37) & key_in(54) & key_in(44) & 
				key_in(27) & key_in(5) & key_in(61) & key_in(30) & key_in(29) & key_in(11) & key_in(36) & key_in(12)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K7 <= key_in(51) & key_in(56) & key_in(10) & key_in(0) & key_in(25) & key_in(58) & key_in(9) & key_in(33) & 
				key_in(43) & key_in(50) & key_in(24) & key_in(18) & key_in(8) & key_in(40) & key_in(2) & key_in(1) & 
				key_in(49) & key_in(34) & key_in(35) & key_in(42) & key_in(41) & key_in(32) & key_in(59) & key_in(17) & 
				key_in(27) & key_in(6) & key_in(13) & key_in(28) & key_in(46) & key_in(45) & key_in(21) & key_in(4) & 
				key_in(14) & key_in(62) & key_in(60) & key_in(38) & key_in(3) & key_in(30) & key_in(12) & key_in(37) & 
				key_in(52) & key_in(61) & key_in(54) & key_in(19) & key_in(22) & key_in(36) & key_in(29) & key_in(5)
				WHEN '1',
				key_in(40) & key_in(16) & key_in(35) & key_in(25) & key_in(50) & key_in(18) & key_in(34) & key_in(58) & 
				key_in(32) & key_in(10) & key_in(49) & key_in(43) & key_in(33) & key_in(0) & key_in(56) & key_in(26) & 
				key_in(9) & key_in(59) & key_in(24) & key_in(2) & key_in(1) & key_in(57) & key_in(48) & key_in(42) & 
				key_in(54) & key_in(29) & key_in(36) & key_in(19) & key_in(6) & key_in(5) & key_in(44) & key_in(62) & 
				key_in(37) & key_in(22) & key_in(20) & key_in(61) & key_in(30) & key_in(53) & key_in(3) & key_in(60) & 
				key_in(12) & key_in(21) & key_in(14) & key_in(46) & key_in(45) & key_in(27) & key_in(52) & key_in(28)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K8 <= key_in(35) & key_in(40) & key_in(59) & key_in(49) & key_in(9) & key_in(42) & key_in(58) & key_in(17) & 
				key_in(56) & key_in(34) & key_in(8) & key_in(2) & key_in(57) & key_in(24) & key_in(51) & key_in(50) & 
				key_in(33) & key_in(18) & key_in(48) & key_in(26) & key_in(25) & key_in(16) & key_in(43) & key_in(1) & 
				key_in(11) & key_in(53) & key_in(60) & key_in(12) & key_in(30) & key_in(29) & key_in(5) & key_in(19) & 
				key_in(61) & key_in(46) & key_in(44) & key_in(22) & key_in(54) & key_in(14) & key_in(27) & key_in(21) & 
				key_in(36) & key_in(45) & key_in(38) & key_in(3) & key_in(6) & key_in(20) & key_in(13) & key_in(52)
				WHEN '1',
				key_in(56) & key_in(32) & key_in(51) & key_in(41) & key_in(1) & key_in(34) & key_in(50) & key_in(9) & 
				key_in(48) & key_in(26) & key_in(0) & key_in(59) & key_in(49) & key_in(16) & key_in(43) & key_in(42) & 
				key_in(25) & key_in(10) & key_in(40) & key_in(18) & key_in(17) & key_in(8) & key_in(35) & key_in(58) & 
				key_in(3) & key_in(45) & key_in(52) & key_in(4) & key_in(22) & key_in(21) & key_in(60) & key_in(11) & 
				key_in(53) & key_in(38) & key_in(36) & key_in(14) & key_in(46) & key_in(6) & key_in(19) & key_in(13) & 
				key_in(28) & key_in(37) & key_in(30) & key_in(62) & key_in(61) & key_in(12) & key_in(5) & key_in(44)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
				
		
						
WITH function_select SELECT
		K9 <= key_in(56) & key_in(32) & key_in(51) & key_in(41) & key_in(1) & key_in(34) & key_in(50) & key_in(9) & 
				key_in(48) & key_in(26) & key_in(0) & key_in(59) & key_in(49) & key_in(16) & key_in(43) & key_in(42) & 
				key_in(25) & key_in(10) & key_in(40) & key_in(18) & key_in(17) & key_in(8) & key_in(35) & key_in(58) & 
				key_in(3) & key_in(45) & key_in(52) & key_in(4) & key_in(22) & key_in(21) & key_in(60) & key_in(11) & 
				key_in(53) & key_in(38) & key_in(36) & key_in(14) & key_in(46) & key_in(6) & key_in(19) & key_in(13) & 
				key_in(28) & key_in(37) & key_in(30) & key_in(62) & key_in(61) & key_in(12) & key_in(5) & key_in(44)
				WHEN '1',
				key_in(35) & key_in(40) & key_in(59) & key_in(49) & key_in(9) & key_in(42) & key_in(58) & key_in(17) & 
				key_in(56) & key_in(34) & key_in(8) & key_in(2) & key_in(57) & key_in(24) & key_in(51) & key_in(50) & 
				key_in(33) & key_in(18) & key_in(48) & key_in(26) & key_in(25) & key_in(16) & key_in(43) & key_in(1) & 
				key_in(11) & key_in(53) & key_in(60) & key_in(12) & key_in(30) & key_in(29) & key_in(5) & key_in(19) & 
				key_in(61) & key_in(46) & key_in(44) & key_in(22) & key_in(54) & key_in(14) & key_in(27) & key_in(21) & 
				key_in(36) & key_in(45) & key_in(38) & key_in(3) & key_in(6) & key_in(20) & key_in(13) & key_in(52)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
						
WITH function_select SELECT
		K10 <= key_in(40) & key_in(16) & key_in(35) & key_in(25) & key_in(50) & key_in(18) & key_in(34) & key_in(58) & 
				key_in(32) & key_in(10) & key_in(49) & key_in(43) & key_in(33) & key_in(0) & key_in(56) & key_in(26) & 
				key_in(9) & key_in(59) & key_in(24) & key_in(2) & key_in(1) & key_in(57) & key_in(48) & key_in(42) & 
				key_in(54) & key_in(29) & key_in(36) & key_in(19) & key_in(6) & key_in(5) & key_in(44) & key_in(62) & 
				key_in(37) & key_in(22) & key_in(20) & key_in(61) & key_in(30) & key_in(53) & key_in(3) & key_in(60) & 
				key_in(12) & key_in(21) & key_in(14) & key_in(46) & key_in(45) & key_in(27) & key_in(52) & key_in(28)
				WHEN '1',
				key_in(51) & key_in(56) & key_in(10) & key_in(0) & key_in(25) & key_in(58) & key_in(9) & key_in(33) & 
				key_in(43) & key_in(50) & key_in(24) & key_in(18) & key_in(8) & key_in(40) & key_in(2) & key_in(1) & 
				key_in(49) & key_in(34) & key_in(35) & key_in(42) & key_in(41) & key_in(32) & key_in(59) & key_in(17) & 
				key_in(27) & key_in(6) & key_in(13) & key_in(28) & key_in(46) & key_in(45) & key_in(21) & key_in(4) & 
				key_in(14) & key_in(62) & key_in(60) & key_in(38) & key_in(3) & key_in(30) & key_in(12) & key_in(37) & 
				key_in(52) & key_in(61) & key_in(54) & key_in(19) & key_in(22) & key_in(36) & key_in(29) & key_in(5)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K11 <= key_in(24) & key_in(0) & key_in(48) & key_in(9) & key_in(34) & key_in(2) & key_in(18) & key_in(42) & 
				key_in(16) & key_in(59) & key_in(33) & key_in(56) & key_in(17) & key_in(49) & key_in(40) & key_in(10) & 
				key_in(58) & key_in(43) & key_in(8) & key_in(51) & key_in(50) & key_in(41) & key_in(32) & key_in(26) & 
				key_in(38) & key_in(13) & key_in(20) & key_in(3) & key_in(53) & key_in(52) & key_in(28) & key_in(46) & 
				key_in(21) & key_in(6) & key_in(4) & key_in(45) & key_in(14) & key_in(37) & key_in(54) & key_in(44) & 
				key_in(27) & key_in(5) & key_in(61) & key_in(30) & key_in(29) & key_in(11) & key_in(36) & key_in(12)
				WHEN '1',
				key_in(2) & key_in(43) & key_in(26) & key_in(16) & key_in(41) & key_in(9) & key_in(25) & key_in(49) & 
				key_in(59) & key_in(1) & key_in(40) & key_in(34) & key_in(24) & key_in(56) & key_in(18) & key_in(17) & 
				key_in(0) & key_in(50) & key_in(51) & key_in(58) & key_in(57) & key_in(48) & key_in(10) & key_in(33) & 
				key_in(12) & key_in(22) & key_in(29) & key_in(44) & key_in(62) & key_in(61) & key_in(37) & key_in(20) & 
				key_in(30) & key_in(11) & key_in(13) & key_in(54) & key_in(19) & key_in(46) & key_in(28) & key_in(53) & 
				key_in(5) & key_in(14) & key_in(3) & key_in(4) & key_in(38) & key_in(52) & key_in(45) & key_in(21)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K12 <= key_in(8) & key_in(49) & key_in(32) & key_in(58) & key_in(18) & key_in(51) & key_in(2) & key_in(26) & 
				key_in(0) & key_in(43) & key_in(17) & key_in(40) & key_in(1) & key_in(33) & key_in(24) & key_in(59) & 
				key_in(42) & key_in(56) & key_in(57) & key_in(35) & key_in(34) & key_in(25) & key_in(16) & key_in(10) & 
				key_in(22) & key_in(60) & key_in(4) & key_in(54) & key_in(37) & key_in(36) & key_in(12) & key_in(30) & 
				key_in(5) & key_in(53) & key_in(19) & key_in(29) & key_in(61) & key_in(21) & key_in(38) & key_in(28) & 
				key_in(11) & key_in(52) & key_in(45) & key_in(14) & key_in(13) & key_in(62) & key_in(20) & key_in(27)
				WHEN '1',
				key_in(18) & key_in(59) & key_in(42) & key_in(32) & key_in(57) & key_in(25) & key_in(41) & key_in(0) & 
				key_in(10) & key_in(17) & key_in(56) & key_in(50) & key_in(40) & key_in(43) & key_in(34) & key_in(33) & 
				key_in(16) & key_in(1) & key_in(2) & key_in(9) & key_in(8) & key_in(35) & key_in(26) & key_in(49) & 
				key_in(28) & key_in(38) & key_in(45) & key_in(60) & key_in(11) & key_in(14) & key_in(53) & key_in(36) & 
				key_in(46) & key_in(27) & key_in(29) & key_in(3) & key_in(4) & key_in(62) & key_in(44) & key_in(6) & 
				key_in(21) & key_in(30) & key_in(19) & key_in(20) & key_in(54) & key_in(5) & key_in(61) & key_in(37)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K13 <= key_in(57) & key_in(33) & key_in(16) & key_in(42) & key_in(2) & key_in(35) & key_in(51) & key_in(10) & 
				key_in(49) & key_in(56) & key_in(1) & key_in(24) & key_in(50) & key_in(17) & key_in(8) & key_in(43) & 
				key_in(26) & key_in(40) & key_in(41) & key_in(48) & key_in(18) & key_in(9) & key_in(0) & key_in(59) & 
				key_in(6) & key_in(44) & key_in(19) & key_in(38) & key_in(21) & key_in(20) & key_in(27) & key_in(14) & 
				key_in(52) & key_in(37) & key_in(3) & key_in(13) & key_in(45) & key_in(5) & key_in(22) & key_in(12) & 
				key_in(62) & key_in(36) & key_in(29) & key_in(61) & key_in(60) & key_in(46) & key_in(4) & key_in(11)
				WHEN '1',
				key_in(34) & key_in(10) & key_in(58) & key_in(48) & key_in(8) & key_in(41) & key_in(57) & key_in(16) & 
				key_in(26) & key_in(33) & key_in(43) & key_in(1) & key_in(56) & key_in(59) & key_in(50) & key_in(49) & 
				key_in(32) & key_in(17) & key_in(18) & key_in(25) & key_in(24) & key_in(51) & key_in(42) & key_in(0) & 
				key_in(44) & key_in(54) & key_in(61) & key_in(13) & key_in(27) & key_in(30) & key_in(6) & key_in(52) & 
				key_in(62) & key_in(12) & key_in(45) & key_in(19) & key_in(20) & key_in(11) & key_in(60) & key_in(22) & 
				key_in(37) & key_in(46) & key_in(4) & key_in(36) & key_in(3) & key_in(21) & key_in(14) & key_in(53)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K14 <= key_in(41) & key_in(17) & key_in(0) & key_in(26) & key_in(51) & key_in(48) & key_in(35) & key_in(59) & 
				key_in(33) & key_in(40) & key_in(50) & key_in(8) & key_in(34) & key_in(1) & key_in(57) & key_in(56) & 
				key_in(10) & key_in(24) & key_in(25) & key_in(32) & key_in(2) & key_in(58) & key_in(49) & key_in(43) & 
				key_in(53) & key_in(28) & key_in(3) & key_in(22) & key_in(5) & key_in(4) & key_in(11) & key_in(61) & 
				key_in(36) & key_in(21) & key_in(54) & key_in(60) & key_in(29) & key_in(52) & key_in(6) & key_in(27) & 
				key_in(46) & key_in(20) & key_in(13) & key_in(45) & key_in(44) & key_in(30) & key_in(19) & key_in(62)
				WHEN '1',
				key_in(50) & key_in(26) & key_in(9) & key_in(35) & key_in(24) & key_in(57) & key_in(8) & key_in(32) & 
				key_in(42) & key_in(49) & key_in(59) & key_in(17) & key_in(43) & key_in(10) & key_in(1) & key_in(0) & 
				key_in(48) & key_in(33) & key_in(34) & key_in(41) & key_in(40) & key_in(2) & key_in(58) & key_in(16) & 
				key_in(60) & key_in(3) & key_in(14) & key_in(29) & key_in(12) & key_in(46) & key_in(22) & key_in(5) & 
				key_in(11) & key_in(28) & key_in(61) & key_in(4) & key_in(36) & key_in(27) & key_in(13) & key_in(38) & 
				key_in(53) & key_in(62) & key_in(20) & key_in(52) & key_in(19) & key_in(37) & key_in(30) & key_in(6)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
		
						
WITH function_select SELECT
		K15 <= key_in(25) & key_in(1) & key_in(49) & key_in(10) & key_in(35) & key_in(32) & key_in(48) & key_in(43) & 
				key_in(17) & key_in(24) & key_in(34) & key_in(57) & key_in(18) & key_in(50) & key_in(41) & key_in(40) & 
				key_in(59) & key_in(8) & key_in(9) & key_in(16) & key_in(51) & key_in(42) & key_in(33) & key_in(56) & 
				key_in(37) & key_in(12) & key_in(54) & key_in(6) & key_in(52) & key_in(19) & key_in(62) & key_in(45) & 
				key_in(20) & key_in(5) & key_in(38) & key_in(44) & key_in(13) & key_in(36) & key_in(53) & key_in(11) & 
				key_in(30) & key_in(4) & key_in(60) & key_in(29) & key_in(28) & key_in(14) & key_in(3) & key_in(46)
				WHEN '1',
				key_in(1) & key_in(42) & key_in(25) & key_in(51) & key_in(40) & key_in(8) & key_in(24) & key_in(48) & 
				key_in(58) & key_in(0) & key_in(10) & key_in(33) & key_in(59) & key_in(26) & key_in(17) & key_in(16) & 
				key_in(35) & key_in(49) & key_in(50) & key_in(57) & key_in(56) & key_in(18) & key_in(9) & key_in(32) & 
				key_in(13) & key_in(19) & key_in(30) & key_in(45) & key_in(28) & key_in(62) & key_in(38) & key_in(21) & 
				key_in(27) & key_in(44) & key_in(14) & key_in(20) & key_in(52) & key_in(12) & key_in(29) & key_in(54) & 
				key_in(6) & key_in(11) & key_in(36) & key_in(5) & key_in(4) & key_in(53) & key_in(46) & key_in(22)
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
						
WITH function_select SELECT
		K16 <= key_in(17) & key_in(58) & key_in(41) & key_in(2) & key_in(56) & key_in(24) & key_in(40) & key_in(35) & 
				key_in(9) & key_in(16) & key_in(26) & key_in(49) & key_in(10) & key_in(42) & key_in(33) & key_in(32) & 
				key_in(51) & key_in(0) & key_in(1) & key_in(8) & key_in(43) & key_in(34) & key_in(25) & key_in(48) & 
				key_in(29) & key_in(4) & key_in(46) & key_in(61) & key_in(44) & key_in(11) & key_in(54) & key_in(37) & 
				key_in(12) & key_in(60) & key_in(30) & key_in(36) & key_in(5) & key_in(28) & key_in(45) & key_in(3) & 
				key_in(22) & key_in(27) & key_in(52) & key_in(21) & key_in(20) & key_in(6) & key_in(62) & key_in(38)
				WHEN '1',
				key_in(9) & key_in(50) & key_in(33) & key_in(59) & key_in(48) & key_in(16) & key_in(32) & key_in(56) & 
				key_in(1) & key_in(8) & key_in(18) & key_in(41) & key_in(2) & key_in(34) & key_in(25) & key_in(24) & 
				key_in(43) & key_in(57) & key_in(58) & key_in(0) & key_in(35) & key_in(26) & key_in(17) & key_in(40) & 
				key_in(21) & key_in(27) & key_in(38) & key_in(53) & key_in(36) & key_in(3) & key_in(46) & key_in(29) & 
				key_in(4) & key_in(52) & key_in(22) & key_in(28) & key_in(60) & key_in(20) & key_in(37) & key_in(62) & 
				key_in(14) & key_in(19) & key_in(44) & key_in(13) & key_in(12) & key_in(61) & key_in(54) & key_in(30)	
				WHEN '0',
				"000000000000000000000000000000000000000000000001" WHEN OTHERS;
				


						
			L_in_internal <= 	data_in(57) & data_in(49) & data_in(41) & data_in(33) & data_in(25) & data_in(17) & 
												data_in(9) & data_in(1) & data_in(59) & data_in(51) & data_in(43) & data_in(35) & 
												data_in(27) & data_in(19) & data_in(11) & data_in(3) & data_in(61) & data_in(53) & 
												data_in(45) & data_in(37) & data_in(29) & data_in(21) & data_in(13) & data_in(5) & 
												data_in(63) & data_in(55) & data_in(47) & data_in(39) & data_in(31) & data_in(23) & 
												data_in(15) & data_in(7);
						
			R_in_internal <= 	data_in(56) & data_in(48) & data_in(40) & data_in(32) & data_in(24) & data_in(16) & 
												data_in(8) & data_in(0) & data_in(58) & data_in(50) & data_in(42) & data_in(34) & 
												data_in(26) & data_in(18) & data_in(10) & data_in(2) & data_in(60) & data_in(52) & 
												data_in(44) & data_in(36) & data_in(28) & data_in(20) & data_in(12) & data_in(4) & 
												data_in(62) & data_in(54) & data_in(46) & data_in(38) & data_in(30) & data_in(22) & 
												data_in(14) & data_in(6);												
						
	


-- rundy DESa
BLOCKTOP1: block_top 
port map (
		L_in 				=> L_in_internal,
		R_in 				=> R_in_internal,
		
		round_key_des 	=> K1,
		
		L_out 			=> L1,
		R_out 			=>	R1	

);

BLOCKTOP2: block_top 
port map (
		L_in 				=> L1,
		R_in 				=> R1,

		round_key_des 	=> K2,	

		L_out 			=> L2,
		R_out 			=>	R2	

);

BLOCKTOP3: block_top 
port map (
		L_in 				=> L2,
		R_in 				=> R2,

		round_key_des 	=> K3,	

		L_out 			=> L3,
		R_out 			=>	R3	

);


BLOCKTOP4: block_top 
port map (
		L_in 				=> L3,
		R_in 				=> R3,

		round_key_des 	=> K4,	

		L_out 			=> L4,
		R_out 			=>	R4	

);


BLOCKTOP5: block_top 
port map (
		L_in 				=> L4,
		R_in 				=> R4,

		round_key_des 	=> K5,	

		L_out 			=> L5,
		R_out 			=>	R5	

);


BLOCKTOP6: block_top 
port map (
		L_in 				=> L5,
		R_in 				=> R5,

		round_key_des 	=> K6,	

		L_out 			=> L6,
		R_out 			=>	R6	

);


BLOCKTOP7: block_top 
port map (
		L_in 				=> L6,
		R_in 				=> R6,

		round_key_des 	=> K7,	

		L_out 			=> L7,
		R_out 			=>	R7	

);


BLOCKTOP8: block_top 
port map (
		L_in 				=> L7,
		R_in 				=> R7,

		round_key_des 	=> K8,	

		L_out 			=> L8,
		R_out 			=>	R8	

);


BLOCKTOP9: block_top 
port map (
		L_in 				=> L8,
		R_in 				=> R8,

		round_key_des 	=> K9,	

		L_out 			=> L9,
		R_out 			=>	R9	

);


BLOCKTOP10: block_top 
port map (
		L_in 				=> L9,
		R_in 				=> R9,

		round_key_des 	=> K10,	

		L_out 			=> L10,
		R_out 			=>	R10	

);


BLOCKTOP11: block_top 
port map (
		L_in 				=> L10,
		R_in 				=> R10,

		round_key_des 	=> K11,	

		L_out 			=> L11,
		R_out 			=>	R11	

);


BLOCKTOP12: block_top 
port map (
		L_in 				=> L11,
		R_in 				=> R11,

		round_key_des 	=> K12,	

		L_out 			=> L12,
		R_out 			=>	R12	

);


BLOCKTOP13: block_top 
port map (
		L_in 				=> L12,
		R_in 				=> R12,

		round_key_des 	=> K13,	

		L_out 			=> L13,
		R_out 			=>	R13	

);


BLOCKTOP14: block_top 
port map (
		L_in 				=> L13,
		R_in 				=> R13,

		round_key_des 	=> K14,	

		L_out 			=> L14,
		R_out 			=>	R14	

);


BLOCKTOP15: block_top 
port map (
		L_in 				=> L14,
		R_in 				=> R14,

		round_key_des 	=> K15,	

		L_out 			=> L15,
		R_out 			=>	R15	

);


BLOCKTOP16: block_top 
port map (
		L_in 				=> L15,
		R_in 				=> R15,

		round_key_des 	=> K16,	

		L_out 			=> L16,
		R_out 			=>	R16	

);


L_out_internal <= L16;
R_out_internal <= R16;

data_out  <=  L_out_internal(7) & R_out_internal(7) & L_out_internal(15) & R_out_internal(15) & -- ostatnia permutacja
                      L_out_internal(23) & R_out_internal(23) & L_out_internal(31) & R_out_internal(31) & 
                      L_out_internal(6) & R_out_internal(6) & L_out_internal(14) & R_out_internal(14) & 
                      L_out_internal(22) & R_out_internal(22) & L_out_internal(30) & R_out_internal(30) & 
                      L_out_internal(5) & R_out_internal(5) & L_out_internal(13) & R_out_internal(13) & 
                      L_out_internal(21) & R_out_internal(21) & L_out_internal(29) & R_out_internal(29) & 
                      L_out_internal(4) & R_out_internal(4) & L_out_internal(12) & R_out_internal(12) & 
                      L_out_internal(20) & R_out_internal(20) & L_out_internal(28) & R_out_internal(28) & 
                      L_out_internal(3) & R_out_internal(3) & L_out_internal(11) & R_out_internal(11) & 
                      L_out_internal(19) & R_out_internal(19) & L_out_internal(27) & R_out_internal(27) & 
                      L_out_internal(2) & R_out_internal(2) & L_out_internal(10) & R_out_internal(10) & 
                      L_out_internal(18) & R_out_internal(18) & L_out_internal(26) & R_out_internal(26) & 
                      L_out_internal(1) & R_out_internal(1) & L_out_internal(9) & R_out_internal(9) & 
                      L_out_internal(17) & R_out_internal(17) & L_out_internal(25) & R_out_internal(25) & 
                      L_out_internal(0) & R_out_internal(0) & L_out_internal(8) & R_out_internal(8) & 
                      L_out_internal(16) & R_out_internal(16) & L_out_internal(24) & R_out_internal(24);
							 
des_out_rdy <= '1';

end Behavioral;
