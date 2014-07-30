library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all; 

entity Xandxor_pipe_correct is
	generic(width	: integer:=8);
	port(	clk		: in std_logic;
			A,B,C		: in std_logic_vector(width-1 downto 0);
			D			: out std_logic_vector(width-1 downto 0)
			);
end Xandxor_pipe_correct;

architecture Behavioral of Xandxor_pipe_correct is
signal pipe : std_logic_vector(width-1 downto 0);
signal Cpipe : std_logic_vector(width-1 downto 0);
begin
	X: process(clk)
	begin
		if rising_edge(clk) then
			pipe	<= A xor B;
			Cpipe <= C;
			D<= pipe and Cpipe;
		end if;
	end process X;
end Behavioral;