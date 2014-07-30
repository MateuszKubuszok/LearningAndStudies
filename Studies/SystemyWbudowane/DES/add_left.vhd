
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity add_left is
port(
    		x_in: in std_logic_vector(0 to 31);
			left_in: in std_logic_vector(0 to 31);
			x_out: out std_logic_vector(0 to 31)
 	);
end add_left;

architecture Behavioral of add_left is

begin

	x_out <= x_in xor left_in;

end Behavioral;
