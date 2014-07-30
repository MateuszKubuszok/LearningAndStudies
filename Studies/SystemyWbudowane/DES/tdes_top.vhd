library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_ARITH.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity tdes_top is
port(
		-- klucze
		key1_in:		in std_logic_vector(0 to 63);
		key2_in:		in std_logic_vector(0 to 63);
		key3_in:		in std_logic_vector(0 to 63);
		
		-- wybór funkcji
		function_select:	in	std_logic; -- 0 - szyfrowanie, 1 - deszyfrowanie
		
		-- dane wejœciowe
		data_in:		in std_logic_vector(0 to 63);
		
		-- dane wyjœciowe
		data_out:	out std_logic_vector(0 to 63);

		-- iterfejs mikrokontrolera
		in_ready:   in    std_logic;   -- wejœcie gotowe
		out_ready:	out	std_logic	-- szyfrowanie danych zakoñczone	
	);
end tdes_top;

architecture Behavioral of tdes_top is

component des_top is
port(
		-- klucz
		key_in:		in std_logic_vector(0 to 63);

		-- wybór funkcji
		function_select:	in	std_logic; -- 1 - szyfrowanie, 0 - deszyfrowanie
		
		-- wejœcie
		data_in:		in std_logic_vector(0 to 63);
		
		-- wyjœcie
		data_out:	out std_logic_vector(0 to 63);

		-- iterfejs mikrokontrollera
		des_out_rdy:	out	std_logic	-- szyfrowanie zakoñczone
	);
end component;

signal key1_in_internal: std_logic_vector(0 to 63);
signal key2_in_internal: std_logic_vector(0 to 63);
signal key3_in_internal: std_logic_vector(0 to 63);
signal fsel_internal: std_logic;
signal fsel_internal_inv: std_logic;
signal des_out_rdy_internal: std_logic;
signal des_out_rdy_internal1: std_logic; 
signal des_out_rdy_internal2: std_logic;
signal data_in_internal: std_logic_vector(0 to 63);
signal data_out_internal: std_logic_vector(0 to 63);
signal data_out_internal1: std_logic_vector(0 to 63);
signal data_out_internal2: std_logic_vector(0 to 63);

begin

DESCIPHERTOP1: des_top 
port map (
		function_select	=> fsel_internal,
		key_in				=> key1_in_internal,
		data_in				=> data_in_internal,
		data_out => data_out_internal1,
		des_out_rdy => des_out_rdy_internal1
);

DESCIPHERTOP2: des_top 
port map (
		function_select => fsel_internal_inv,
		key_in => key2_in_internal,
		data_in => data_out_internal1,
		data_out => data_out_internal2,
		des_out_rdy => des_out_rdy_internal2
);

DESCIPHERTOP3: des_top 
port map (
		function_select => fsel_internal,
		key_in => key3_in_internal,
		data_in => data_out_internal2,
		data_out => data_out_internal,
		des_out_rdy => des_out_rdy_internal	
);

with in_ready select out_ready <=
	'0' 						when '0',
	des_out_rdy_internal when '1',
	'0'						when others;
	
fsel_internal <= function_select;
fsel_internal_inv	<= not function_select;

with fsel_internal select key1_in_internal <= 
	key3_in when '0',
	key1_in when '1',
	key1_in when others;
key2_in_internal	<= key2_in;
with fsel_internal select key3_in_internal <= 
	key1_in when '0',
	key3_in when '1',
	key3_in when others;
data_in_internal	<= data_in;
data_out				<= data_out_internal;

end Behavioral;