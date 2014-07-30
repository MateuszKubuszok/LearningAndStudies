library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all; 

entity Xxor is
	generic(width	: integer:=8);
	port(	clk		: in std_logic;
			A,B		: in std_logic_vector(width-1 downto 0);
			C			: out std_logic_vector(width-1 downto 0)
			);
end Xxor;

architecture Behavioral of Xxor is
begin
	C<= A xor B;
end Behavioral;