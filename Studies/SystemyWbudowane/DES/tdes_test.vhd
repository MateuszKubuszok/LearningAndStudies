LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY tdes_test IS
END tdes_test;
 
ARCHITECTURE behavior OF tdes_test IS 
 
    COMPONENT tdes_top
    PORT(
         key1_in : IN  std_logic_vector(0 to 63);
         key2_in : IN  std_logic_vector(0 to 63);
         key3_in : IN  std_logic_vector(0 to 63);
         function_select : IN  std_logic;
         data_in : IN  std_logic_vector(0 to 63);
         data_out : OUT  std_logic_vector(0 to 63);
         in_ready : IN  std_logic;
         out_ready : OUT  std_logic
        );
    END COMPONENT;

   -- Wejœcie
   signal key1_in : std_logic_vector(0 to 63);
   signal key2_in : std_logic_vector(0 to 63);
   signal key3_in : std_logic_vector(0 to 63);
   signal function_select : std_logic;
   signal data_in : std_logic_vector(0 to 63);
   signal in_ready : std_logic;

 	-- Wyjœcie
   signal data_out : std_logic_vector(0 to 63);
   signal out_ready : std_logic;
 
BEGIN
   uut: tdes_top PORT MAP (
          key1_in => key1_in,
          key2_in => key2_in,
          key3_in => key3_in,
          function_select => function_select,
          data_in => data_in,
			 in_ready => in_ready,
          data_out => data_out,
          out_ready => out_ready
        );
 
   stim_proc: PROCESS
   BEGIN
      WAIT FOR 100 ns;
		
		function_select <= '1';
		key1_in <= "0001001100110100010101110111100110011011101111001101111111110001";
		key2_in <= "0100100101010101000101101010101010101010110010101010101010100101";
		key3_in <= "0001100001000000111110011100101111111000000111111010110000000011";
		data_in <= "0101010101010101010101010101010101010101010101010101010101010101";
		in_ready <= '1';

      WAIT;
   END PROCESS;

END;
