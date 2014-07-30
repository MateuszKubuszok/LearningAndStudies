
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity p_box is
port(
    		x0_in: in std_logic_vector(3 downto 0);
			x1_in: in std_logic_vector(3 downto 0);
			x2_in: in std_logic_vector(3 downto 0);
			x3_in: in std_logic_vector(3 downto 0);
			x4_in: in std_logic_vector(3 downto 0);
			x5_in: in std_logic_vector(3 downto 0);
			x6_in: in std_logic_vector(3 downto 0);
			x7_in: in std_logic_vector(3 downto 0);
			x_out: out std_logic_vector(0 to 31)
 	);
end p_box;

architecture Behavioral of p_box is

signal x_in: std_logic_vector(0 to 31);

begin

		x_in 	<= x0_in & x1_in & x2_in & x3_in & x4_in & x5_in & x6_in & x7_in;
		x_out <= x_in(15) & x_in(6) & x_in(19) & x_in(20) & x_in(28) & x_in(11) & 
					x_in(27) & x_in(16) & x_in(0) & x_in(14) & x_in(22) & x_in(25) & 
					x_in(4) & x_in(17) & x_in(30) & x_in(9) & x_in(1) & x_in(7) & 
					x_in(23) & x_in(13) & x_in(31) & x_in(26) & x_in(2) & x_in(8) & 
					x_in(18) & x_in(12) & x_in(29) & x_in(5) & x_in(21) & x_in(10) & 
					x_in(3) & x_in(24);

end Behavioral;
