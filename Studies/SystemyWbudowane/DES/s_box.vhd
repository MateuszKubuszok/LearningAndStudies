
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity s_box is
port(
    		block0_in: in std_logic_vector(5 downto 0);
			block1_in: in std_logic_vector(5 downto 0);
			block2_in: in std_logic_vector(5 downto 0);
			block3_in: in std_logic_vector(5 downto 0);
			block4_in: in std_logic_vector(5 downto 0);
			block5_in: in std_logic_vector(5 downto 0);
			block6_in: in std_logic_vector(5 downto 0);
			block7_in: in std_logic_vector(5 downto 0);
			x0_out: out std_logic_vector(3 downto 0);
			x1_out: out std_logic_vector(3 downto 0);
			x2_out: out std_logic_vector(3 downto 0);
			x3_out: out std_logic_vector(3 downto 0);
			x4_out: out std_logic_vector(3 downto 0);
			x5_out: out std_logic_vector(3 downto 0);
			x6_out: out std_logic_vector(3 downto 0);
			x7_out: out std_logic_vector(3 downto 0)
 	);
end s_box;

architecture Behavioral of s_box is

component s1_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;		

component s2_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s3_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s4_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s5_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s6_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s7_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

component s8_box
	port(
 	a: in  std_logic_VECTOR(5 downto 0);
	spo: out std_logic_VECTOR(3 downto 0)
	);
end component;

begin

S1 : s1_box 
	port map (
		a => block0_in,
		spo => x0_out	
);

S2 : s2_box 
	port map (
		a => block1_in,
		spo => x1_out	
);

S3 : s3_box 
	port map (
		a => block2_in,
		spo => x2_out	
);

S4 : s4_box 
	port map (
		a => block3_in,
		spo => x3_out	
);

S5 : s5_box 
	port map (
		a => block4_in,
		spo => x4_out	
);

S6 : s6_box 
	port map (
		a => block5_in,
		spo => x5_out	
);

S7 : s7_box 
	port map (
		a => block6_in,
		spo => x6_out	
);

S8 : s8_box 
	port map (
		a => block7_in,
		spo => x7_out	
);

end Behavioral;
