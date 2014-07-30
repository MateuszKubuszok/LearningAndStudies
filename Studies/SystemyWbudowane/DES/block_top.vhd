
library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity block_top is
port(
		
		L_in: in std_logic_vector(0 to 31);
		R_in: in std_logic_vector(0 to 31);
	
		
		L_out: out std_logic_vector(0 to 31);
		R_out: out std_logic_vector(0 to 31);

     	
		round_key_des: in std_logic_vector(0 to 47)	-- klucz rundy

	);
end block_top;

architecture Behavioral of block_top is

component e_expansion_function
	port(
	    	x_in: in std_logic_vector(0 to 31); -- funkcja rozszerzajaca
    		block0_out: out std_logic_vector(0 to 5);
			block1_out: out std_logic_vector(0 to 5);
			block2_out: out std_logic_vector(0 to 5);
			block3_out: out std_logic_vector(0 to 5);
			block4_out: out std_logic_vector(0 to 5);
			block5_out: out std_logic_vector(0 to 5);
			block6_out: out std_logic_vector(0 to 5);
			block7_out: out std_logic_vector(0 to 5)
 	);
end component;


component add_key
	port(
    		x0_in: in std_logic_vector(0 to 5); -- funkcja xorujaca z kluczem
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
end component;


component s_box
	port(
    		block0_in: in std_logic_vector(5 downto 0); -- funkcja dla sboxow
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
end component;


component p_box
	port(
    		x0_in: in std_logic_vector(3 downto 0); -- permutacje
			x1_in: in std_logic_vector(3 downto 0);
			x2_in: in std_logic_vector(3 downto 0);
			x3_in: in std_logic_vector(3 downto 0);
			x4_in: in std_logic_vector(3 downto 0);
			x5_in: in std_logic_vector(3 downto 0);
			x6_in: in std_logic_vector(3 downto 0);
			x7_in: in std_logic_vector(3 downto 0);
			x_out: out std_logic_vector(0 to 31)
 	);
end component;


component add_left
	port(
    		x_in: in std_logic_vector(0 to 31);
			left_in: in std_logic_vector(0 to 31);
			x_out: out std_logic_vector(0 to 31)
 	);
end component;



signal a0, a1, a2, a3, a4, a5, a6, a7: std_logic_vector(0 to 5);
signal b0, b1, b2, b3, b4, b5, b6, b7: std_logic_vector(5 downto 0);
signal c0, c1, c2, c3, c4, c5, c6, c7: std_logic_vector(3 downto 0);
signal d0: std_logic_vector(0 to 31);
signal R_out_internal: std_logic_vector(0 to 31);

begin

L_out <= R_in;
R_out <= R_out_internal;


E_EXPANSIONFUNCTION : e_expansion_function
port map (
			x_in => R_in,   		
			block0_out => a0,
    		block1_out => a1,
			block2_out => a2,
			block3_out => a3,
			block4_out => a4,
			block5_out => a5,
			block6_out => a6,
			block7_out => a7
);


ADDKEY : add_key
port map (
			x0_in => a0,
    		x1_in => a1,
			x2_in => a2,
			x3_in => a3,
			x4_in => a4,
			x5_in => a5,
			x6_in => a6,
			x7_in => a7,
			key => round_key_des,
			x0_out => b0,
    		x1_out => b1,
			x2_out => b2,
			x3_out => b3,
			x4_out => b4,
			x5_out => b5,
			x6_out => b6,
			x7_out => b7
);


SBOX : s_box
port map (
			block0_in => b0,
    		block1_in => b1,
			block2_in => b2,
			block3_in => b3,
			block4_in => b4,
			block5_in => b5,
			block6_in => b6,
			block7_in => b7,
			x0_out =>  c0,
			x1_out =>  c1,
			x2_out =>  c2,
			x3_out =>  c3,
			x4_out =>  c4,
			x5_out =>  c5,
			x6_out =>  c6,
			x7_out =>  c7
);


PBOX : p_box
port map (
			x0_in => c0,
			x1_in => c1,
			x2_in => c2,
			x3_in => c3,
			x4_in => c4,
			x5_in => c5,
			x6_in => c6,
			x7_in => c7,
			x_out => d0
);



ADDLEFT : add_left
port map (
			x_in => d0,
			left_in => L_in,
			x_out => R_out_internal
);


end Behavioral;
