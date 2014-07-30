
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity add_key is
port(
    		x0_in: in std_logic_vector(0 to 5);
			x1_in: in std_logic_vector(0 to 5);
			x2_in: in std_logic_vector(0 to 5);
			x3_in: in std_logic_vector(0 to 5);
			x4_in: in std_logic_vector(0 to 5);
			x5_in: in std_logic_vector(0 to 5);
			x6_in: in std_logic_vector(0 to 5);
			x7_in: in std_logic_vector(0 to 5);
			key: in std_logic_vector(0 to 47);
			x0_out: out std_logic_vector(5 downto 0);
			x1_out: out std_logic_vector(5 downto 0);
			x2_out: out std_logic_vector(5 downto 0);
			x3_out: out std_logic_vector(5 downto 0);
			x4_out: out std_logic_vector(5 downto 0);
			x5_out: out std_logic_vector(5 downto 0);
			x6_out: out std_logic_vector(5 downto 0);
			x7_out: out std_logic_vector(5 downto 0)
 	);
end add_key;


architecture Behavioral of add_key is
begin

	x0_out <= x0_in xor key(0 to 5);
	x1_out <= x1_in xor key(6 to 11);
	x2_out <= x2_in xor key(12 to 17);
	x3_out <= x3_in xor key(18 to 23);
	x4_out <= x4_in xor key(24 to 29);
	x5_out <= x5_in xor key(30 to 35);
	x6_out <= x6_in xor key(36 to 41);
	x7_out <= x7_in xor key(42 to 47);

end Behavioral;
