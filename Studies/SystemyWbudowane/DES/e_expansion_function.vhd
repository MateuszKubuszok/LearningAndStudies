
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity e_expansion_function is
port(
	    	x_in: in std_logic_vector(0 to 31);
    		block0_out: out std_logic_vector(0 to 5);
			block1_out: out std_logic_vector(0 to 5);
			block2_out: out std_logic_vector(0 to 5);
			block3_out: out std_logic_vector(0 to 5);
			block4_out: out std_logic_vector(0 to 5);
			block5_out: out std_logic_vector(0 to 5);
			block6_out: out std_logic_vector(0 to 5);
			block7_out: out std_logic_vector(0 to 5)
 	);
end e_expansion_function;

architecture Behavioral of e_expansion_function is

begin

		block0_out <= x_in(31) & x_in(0) & x_in(1) & x_in(2) & x_in(3) & x_in(4);	
		block1_out <= x_in(3) & x_in(4) & x_in(5) & x_in(6) & x_in(7) & x_in(8);
		block2_out <= x_in(7) & x_in(8) & x_in(9) & x_in(10) & x_in(11) & x_in(12);
		block3_out <= x_in(11) & x_in(12) & x_in(13) & x_in(14) & x_in(15) & x_in(16);
		block4_out <= x_in(15) & x_in(16) & x_in(17) & x_in(18) & x_in(19) & x_in(20);
		block5_out <= x_in(19) & x_in(20) & x_in(21) & x_in(22) & x_in(23) & x_in(24);
		block6_out <= x_in(23) & x_in(24) & x_in(25) & x_in(26) & x_in(27) & x_in(28);
		block7_out <= x_in(27) & x_in(28) & x_in(29) & x_in(30) & x_in(31) & x_in(0);

end Behavioral;
