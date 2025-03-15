#pragma once

/*
이름: CByteMemory
역할: byte 포인터 메모리 관리 클래스
*/
#define MEMFILL_TABLE_MAX	10	// 대체 테이블 최대 크기
//test2
class CByteMemory
{
public:
	CByteMemory();
	CByteMemory(const byte* pData, int nSize);
	~CByteMemory();

private:
	byte*	m_pBuffer;	// 실제 데이터 포인터
	int		m_nSize;	// m_pBuffer 의 사이즈
	bool	m_bClearMemoryFill;	//clear 시 메모리값을 임의 값으로 초기화
	byte	m_RepTable[MEMFILL_TABLE_MAX];	//치환할 데이터 테이블

public:
	void Clear();	// 클리어
	void Fill(byte* dest, int nDestSize, int nBeginIndex = 0);	//메모리 치환 함수
	void Set(const byte* pData, int nSize);	// 신규 메모리로 설정
	void Set(int nCount, ...);	//size + byte* 여러개
	int Add(const byte* pData, int nSize);	// 기존 메모리에 신규 메모리 추가
	int Add(byte data);	// 기존 메모리에 byte 추가
	byte* TakeIt(int* pnSize = NULL);	// 기존 메모리를 새로 생성하여 반환
	void MemSet(byte c);	// 특정 값으로 초기화
	bool Create(int nNewSize, byte init = 0x00); // 특정 사이즈로 메모리 생성
	byte GetFirst(); //첫자리 값 반환
	byte GetLast();	// 마지막 값 반환
	int CopyFrom(CByteMemory* pMemory); // 다른 CByteMemory 로 부터 메모리 복사
	bool CopyFrom(CByteMemory* pMemory, int nSize);	//다른 CByteMemory 로 부터 주어진 사이즈 만큼 메모리 복사
	bool SetValue(int nPos, byte val);	// 메모리 특정 위치에 값 설정
	bool SetLast(byte val);	// 메모리 마지막 위치에 값 설정
	const byte* GetBuffer(int nPos);
	bool SetBuffer(const byte* src, int nPos, int nSize);
	bool MemCpy(const byte* src, int nSize);	//메모리 카피
	byte* ItLast(int nFromLast); //뒤에서부터 nFromLast 부터 메모리를 반환

public:
	void operator+=(byte c);	// 메모리에 byte 값 추가

public:
	inline const byte* Get() { return m_pBuffer; }	// 메모리를 수정 불가 형태로 반환
	inline byte* It() { return m_pBuffer; } // 메모리를 수정 가능 형태로 반환
	inline int Size() { return m_nSize; }	// 메모리 사이즈 반환
	inline void SetClearMemoryFill(bool bEnable) { m_bClearMemoryFill = bEnable; }	// 삭제 시 메모리를 임의의 값으로 변조 후 변경시킨 후 삭제할 것인지 여부
};


