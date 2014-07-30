LIBRARY ieee;
USE ieee.std_logic_1164.ALL;
 
ENTITY des_test IS
END des_test;
 
ARCHITECTURE behavior OF des_test IS 
 
    COMPONENT des_top
    PORT(
         key_in : IN  std_logic_vector(0 to 63);
         data_in : IN  std_logic_vector(0 to 63);
         function_select : IN  std_logic;
         data_out : OUT  std_logic_vector(0 to 63);
         des_out_rdy : OUT  std_logic
        );
    END COMPONENT;

   -- Wejœcie
   signal key_in : std_logic_vector(0 to 63) := (others => '0');
   signal data_in : std_logic_vector(0 to 63) := (others => '0');
   signal function_select : std_logic := '0';

 	-- Wyjœcie
   signal data_out : std_logic_vector(0 to 63);
   signal des_out_rdy : std_logic;
 
BEGIN
   uut: des_top PORT MAP (
          key_in => key_in,
          data_in => data_in,
          function_select => function_select,
          data_out => data_out,
          des_out_rdy => des_out_rdy
        );

   stim_proc: PROCESS
   BEGIN
      WAIT FOR 100 ns;
		
		function_select <= '1';
		key_in  <= "0001001100110100010101110111100110011011101111001101111111110001";
		data_in <= "0101010101010101010101010101010101010101010101010101010101010101";

      WAIT;
   END PROCESS;
END;

