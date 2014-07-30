library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all; 

entity Xandxor_pipe is
	generic(width	: integer:=8);
	port(	clk		: in std_logic;
			A,B,C		: in std_logic_vector(width-1 downto 0);
			D			: out std_logic_vector(width-1 downto 0)
			);
end Xandxor_pipe;

architecture Behavioral of Xandxor_pipe is
signal pipe : std_logic_vector(width-1 downto 0);
begin
	X: process(clk)
	begin
		if rising_edge(clk) then
			--BUG! this is incorrect, input C arrives at "and" 1 clock cycle earlier than "xor" output - desynchronization!
			pipe	<= A xor B;
			D		<= pipe and C;
		end if;
	end process X;
end Behavioral;