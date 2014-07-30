library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all; 

entity Xandxor is
	generic(width	: integer:=8);
	port(	clk		: in std_logic;
			A,B,C		: in std_logic_vector(width-1 downto 0);
			D			: out std_logic_vector(width-1 downto 0)
			);
end Xandxor;

architecture Behavioral of Xandxor is
begin
	X: process(clk)
	begin
		if rising_edge(clk) then
			D<= (A xor B) and C;
		end if;
	end process X;
end Behavioral;