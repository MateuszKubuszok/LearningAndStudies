library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all; 

entity Xandxor_inst is
	generic(width	: integer:=8);
	port(	clk		: in std_logic;
			A,B,C		: in std_logic_vector(width-1 downto 0);
			D			: out std_logic_vector(width-1 downto 0)
			);
end Xandxor_inst;

architecture Behavioral of Xandxor_inst is
signal Xand_in1	: std_logic_vector(width-1 downto 0);
signal Xand_in2	: std_logic_vector(width-1 downto 0);
signal Xand_out	: std_logic_vector(width-1 downto 0);
signal Xxor_in1	: std_logic_vector(width-1 downto 0);
signal Xxor_in2	: std_logic_vector(width-1 downto 0);
signal Xxor_out	: std_logic_vector(width-1 downto 0);
begin
	Xand: entity WORK.Xand	generic map(width=>width)
									port map(clk=>clk,A=>Xand_in1,B=>Xand_in2,C=>Xand_out);
	Xxor: entity WORK.Xxor	generic map(width=>width)
									port map(clk=>clk,A=>Xxor_in1,B=>Xxor_in2,C=>Xxor_out);
	Xxor_in1<=A;
	Xxor_in2<=B;
	X: process(clk)
	begin
		if rising_edge(clk) then
			Xand_in1<=C;
			Xand_in2<=Xxor_out;
			D<=Xand_out;
		end if;
	end process X;
end Behavioral;