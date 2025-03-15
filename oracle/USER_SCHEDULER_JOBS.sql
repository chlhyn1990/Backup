/*
BEGIN
	SYS.DBMS_SCHEDULER.DROP_JOB
	(job_name  => 'job 이름');
END;


BEGIN
	SYS.DBMS_SCHEDULER.CREATE_JOB
	(
	   job_name        => 'job 이름'
	  ,start_date      => TO_TIMESTAMP_TZ('2021/03/22 12:00:00.000000 +09:00','yyyy/mm/dd hh24:mi:ss.ff tzr')	
	  ,repeat_interval => 'FREQ=DAILY;BYHOUR=12;BYMINUTE=0;BYSECOND=0;'											
	  ,end_date        => NULL
	  ,job_class       => 'DEFAULT_JOB_CLASS'
	  ,job_type        => 'PLSQL_BLOCK'
	  ,job_action      => 'BEGIN 프로시져(''파라미터1'',TO_CHAR(SYSDATE, ''YYYYMMDD'')); COMMIT; END;'
	  ,comments        => '설명'
	);
END;

SYS.DBMS_SCHEDULER.ENABLE
(name => 'job 이름');

--작업이 다시 시작할 수 있는지 여부를 나타냄
/*
이 특성은 오류가 발생할 경우 작업을 다시 시작할 수 있는지 여부를 지정합니다. 
기본적으로 작업을 다시 시작할 수 없으며 이 특성은 로 설정됩니다. 이 설정을 설정하면 실행 중인 작업이 실패하면 작업의 시작 지점에서 다시 시작됩니다.FALSE(TRUE)
*/
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
( name      => 'MNTH_CLOS'
 ,attribute => 'RESTARTABLE'
 ,value     => FALSE);
 
-- 기록된 정보양 지정
/*
DBMS_SCHEDULER.LOGGING_OFF
이 클래스의 모든 작업에 대해 로깅이 수행되지 않음

DBMS_SCHEDULER.LOGGING_FAILED_RUNS
스케줄러는 실패한 클래스의 작업만 기록하며 실패의 원인으로 기록

DBMS_SCHEDULER.LOGGING_RUNS
스케줄러는 이 클래스의 각 작업의 모든 실행에 대한 작업 로그에 자세한 정보를 기록. 기본값

DBMS_SCHEDULER.LOGGING_FULL
스케줄러는 작업의 모든 실행을 기록하는 것 외에도 이 클래스의 모든 작업에서 수행되는 모든 작업을 기록한다
작업이 생성되고 활성화되고 비활성화되고 변경(포함됨) 중지될 때마다 로그에 항목이 기록된다.SET_ATTRIBUTE

*/
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
( name      => 'MNTH_CLOS'
 ,attribute => 'LOGGING_LEVEL'
 ,value     => SYS.DBMS_SCHEDULER.LOGGING_OFF);
 
 
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE_NULL
( name      => 'MNTH_CLOS'
 ,attribute => 'MAX_FAILURES');
 
 
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE_NULL
( name      => 'MNTH_CLOS'
 ,attribute => 'MAX_RUNS');
  
/*
작업의 우선 순위를 지정 
1에서 5까지의 값이 될 수 있으며 1이 가장 먼저
작업을 만들 때 작업 우선 순위를 지정하지 않으면 3 기본
*/
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
( name      => 'MNTH_CLOS'
 ,attribute => 'JOB_PRIORITY'
 ,value     => 3);
 
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE_NULL
( name      => 'MNTH_CLOS'
 ,attribute => 'SCHEDULE_LIMIT');
 
/*
이 플래그가 완료되었거나 자동으로 비활성화된 후 작업이 자동으로 삭제되는 경우 다음과 같은 경우 작업이 완료된 것으로 간주됩니다.TRUE
종료 날짜(또는 일정종료일)가 지났습니다.
그것은 횟수를 실행했습니다. 로 설정해야 합니다.max_runsmax_runsSET_ATTRIBUTE
반복되는 작업이 아니며 한 번 실행되었습니다.
작업이 실패한 경우 사용 중지됩니다. 또한 로 설정되어 있습니다.max_failuresmax_failuresSET_ATTRIBUTE
이 플래그로 설정된 경우 작업이 삭제되지 않고 프로시저와 함께 작업이 명시적으로 삭제될 때까지 메타데이터가 유지됩니다.FALSEDROP_JOB
-- 기본
기본적으로 작업은 로 설정되어 만들어집니다.auto_dropTRUE  
*/
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
( name      => 'MNTH_CLOS'
 ,attribute => 'AUTO_DROP'
 ,value     => TRUE);	
*/
---------------------------------------------------------------------------------------------------------------------------------------
--스케줄러 확인
SELECT 
  job_name, REPEAT_INTERVAL
  , TO_CHAR(last_start_date, 'yyyy-mm-dd hh24:mi:ss')
  , TO_CHAR(next_run_date, 'yyyy-mm-dd hh24:mi:ss')  
FROM user_scheduler_jobs
--WHERE logging_level = 'RUNS'
ORDER BY REPEAT_INTERVAL

--스케줄러명 확인
select * 
from user_scheduler_job_log 
where job_name='JOB_SP_CUST_INAC_SMS'

--스케줄러 생성
BEGIN
    DBMS_SCHEDULER.CREATE_JOB
    (
    JOB_NAME => 'JOB_SP_CUST_INAC_SMS',
    JOB_TYPE => 'PLSQL_BLOCK',
	JOB_CLASS       => 'DEFAULT_JOB_CLASS',
	START_DATE      => TO_TIMESTAMP_TZ('2021/03/24 12:00:00.000000 +09:00','yyyy/mm/dd hh24:mi:ss.ff tzr'),
    JOB_ACTION => 
'
DECLARE
        /* ---------------------------------------------
         * [고객휴면문자] SP_CUST_INAC_SMS
         * 실행 SP : SP_CUST_INAC_SMS
         * 주기 : 매일 03시 00분 생성
         *
         */
            OUT_CODE    VARCHAR2(256) ;  -- 처리결과코드 (0:Success, -1:Fail)
            OUT_MSG     VARCHAR2(4096);  -- 처리메세지
        BEGIN
            -- 회사코드를 가져온다...

            DECLARE CURSOR COMP_DATA IS

                SELECT COMP_CD, COMP_KOR_NM
                FROM CFCOMP
                WHERE COMP_CLOSE_DT IS NULL
                  and comp_cd = ''NG001''
                ORDER BY BUSI_DT;

            BEGIN
                FOR CUR_COMP_DATA IN COMP_DATA LOOP

                  BEGIN
                      SP_CUST_INAC_SMS(CUR_COMP_DATA.COMP_CD, ''SYSTEM'', ''SYSTEM'', OUT_CODE, OUT_MSG);
                  END;

                END LOOP;
            END;
        END;
	
',
    REPEAT_INTERVAL => 'Freq=Daily;ByHour=03;ByMinute=00;BySecond=00',
    COMMENTS => '휴면예정 고객안내 문자'
    );
END;



--스케줄러 사용
exec
  SYS.DBMS_SCHEDULER.ENABLE
    (name                  => 'JOB_SP_CUST_INAC_SMS');

--로그사용
execute
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
    ( name      => 'JOB_SP_CUST_INAC_SMS'
     ,attribute => 'LOGGING_LEVEL'
     ,value     => SYS.DBMS_SCHEDULER.LOGGING_RUNS);

--완료 및 미사용 자동 삭제여부
execute
SYS.DBMS_SCHEDULER.SET_ATTRIBUTE
    ( name      => 'JOB_SP_CUST_INAC_SMS'
     ,attribute => 'AUTO_DROP'
     ,value     => false);

--실행
EXEC dbms_scheduler.run_job('JOB_SP_CUST_INAC_SMS')
	 
--삭제
BEGIN
	dbms_scheduler.drop_job
	(
		job_name =>'JOB_SP_CUST_INAC_SMS',
		FORCE=>false
	);
END;
