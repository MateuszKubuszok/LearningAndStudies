
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

ENTITY s2_box IS
	port (
	A: IN std_logic_VECTOR(5 downto 0);
	SPO: OUT std_logic_VECTOR(3 downto 0));
END s2_box;

architecture Behavioral of s2_box is

begin

SPO 	<= "1111" when A = x"0" else
			"0011" when A = x"1" else
			"0001" when A = x"2" else
			"1101" when A = x"3" else
			"1000" when A = x"4" else
			"0100" when A = x"5" else
			"1110" when A = x"6" else
			"0111" when A = x"7" else
			"0110" when A = x"8" else
			"1111" when A = x"9" else
			"1011" when A = x"A" else
			"0010" when A = x"B" else
			"0011" when A = x"C" else
			"1000" when A = x"D" else
			"0100" when A = x"E" else
			"1110" when A = x"F" else
			"1001" when A = x"10" else
			"1100" when A = x"11" else
			"0111" when A = x"12" else
			"0000" when A = x"13" else
			"0010" when A = x"14" else
			"0001" when A = x"15" else
			"1101" when A = x"16" else
			"1010" when A = x"17" else
			"1100" when A = x"18" else
			"0110" when A = x"19" else
			"0000" when A = x"1A" else
			"1001" when A = x"1B" else
			"0101" when A = x"1C" else
			"1011" when A = x"1D" else
			"1010" when A = x"1E" else
			"0101" when A = x"1F" else
			"0000" when A = x"20" else
			"1101" when A = x"21" else
			"1110" when A = x"22" else
			"1000" when A = x"23" else
			"0111" when A = x"24" else
			"1010" when A = x"25" else
			"1011" when A = x"26" else
			"0001" when A = x"27" else
			"1010" when A = x"28" else
			"0011" when A = x"29" else
			"0100" when A = x"2A" else
			"1111" when A = x"2B" else
			"1101" when A = x"2C" else
			"0100" when A = x"2D" else
			"0001" when A = x"2E" else
			"0010" when A = x"2F" else
			"0101" when A = x"30" else
			"1011" when A = x"31" else
			"1000" when A = x"32" else
			"0110" when A = x"33" else
			"1100" when A = x"34" else
			"0111" when A = x"35" else
			"0110" when A = x"36" else
			"1100" when A = x"37" else
			"1001" when A = x"38" else
			"0000" when A = x"39" else
			"0011" when A = x"3A" else
			"0101" when A = x"3B" else
			"0010" when A = x"3C" else
			"1110" when A = x"3D" else
			"1111" when A = x"3E" else
			"1001" when A = x"3F" else
			"1001";

END Behavioral;
