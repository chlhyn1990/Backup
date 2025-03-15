CREATE OR REPLACE PACKAGE      TEST.CRYPTO
IS
    FUNCTION encrypt (input_string IN VARCHAR2, key_data IN VARCHAR2 := 'test1234') RETURN RAW;
    
    FUNCTION decrypt (input_string IN VARCHAR2, key_data IN VARCHAR2 := 'test1234') RETURN VARCHAR2;

END CRYPTO; 

CREATE OR REPLACE PACKAGE BODY      TEST.CRYPTO
IS
    SQLERRMSG   VARCHAR2(255);
    SQLERRCDE   NUMBER;
     
    FUNCTION encrypt (input_string IN VARCHAR2, key_data IN VARCHAR2 := 'test1234') 
     RETURN RAW
    IS
    
        input_raw RAW(1024);
        key_raw RAW(16) := UTL_RAW.CAST_TO_RAW(key_data);
        v_out_raw RAW(1024);

        AES_CBC_PKCS5 CONSTANT PLS_INTEGER := DBMS_CRYPTO.ENCRYPT_AES128 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5;

    BEGIN
        IF input_string IS NULL THEN
         RETURN NULL;
        end IF;
        
        input_raw := UTL_I18N.STRING_TO_RAW(input_string, 'AL32UTF8');
        v_out_raw := DBMS_CRYPTO.ENCRYPT(
                src => input_raw,
                typ => AES_CBC_PKCS5,
                key => key_raw);

    RETURN v_out_raw;
    END encrypt;
    
    FUNCTION decrypt (input_string IN VARCHAR2, key_data IN VARCHAR2 := 'test1234') 
     RETURN VARCHAR2
    IS
        key_raw RAW(16) := UTL_RAW.CAST_TO_RAW(key_data);
        output_raw RAW(1024);
        v_out_string VARCHAR2(1024);

        AES_CBC_PKCS5 CONSTANT PLS_INTEGER := DBMS_CRYPTO.ENCRYPT_AES128 + DBMS_CRYPTO.CHAIN_CBC + DBMS_CRYPTO.PAD_PKCS5;

    BEGIN
        IF input_string IS NULL THEN
         RETURN NULL;
        end IF;
    
        output_raw := DBMS_CRYPTO.DECRYPT(
                src => input_string,
                typ => AES_CBC_PKCS5,
                key => key_raw);
                
        v_out_string := UTL_I18N.RAW_TO_CHAR(output_raw, 'AL32UTF8');

    RETURN v_out_string;
    END decrypt ;
END CRYPTO; 
