#include "stdafx.h"
#include "ByteMemory.h"

/*
함수: CByteMemory
역할: 생성자. 초기화.
인자:
반환:
*/
CByteMemory::CByteMemory()
	: m_bClearMemoryFill(false)
{
	m_nSize = 0;
	m_pBuffer = NULL;
}

/*
함수: CByteMemory
역할: 생성자. 초기화.
인자: pData (복사할 메모리)
	nSize (복사할 메모리 사이즈)
반환:
*/
CByteMemory::CByteMemory(const byte* pData, int nSize)
{
	m_nSize = 0;
	m_pBuffer = NULL;
	Set(pData, nSize);
}

/*
함수: ~CByteMemory
역할: 소멸자. 메모리 삭제.
인자:
반환:
*/
CByteMemory::~CByteMemory()
{
	Clear();
}

/*
함수: Clear
역할: 메모리 삭제.
인자:
반환:
*/
void CByteMemory::Clear()
{
	if (m_pBuffer != NULL) {
		if (m_bClearMemoryFill == true)
			Fill(m_pBuffer, m_nSize);
		free(m_pBuffer);
	}

	m_pBuffer = NULL;
	m_nSize = 0;
}

/*
함수: Fill
역할: 메모리 값을 테이블 값으로 치환한다.
인자: dest(대상 메모리 주소)
	nDestSize(대상 메모리 사이즈)
반환:
*/
void CByteMemory::Fill(byte * dest, int nDestSize, int nBeginIndex /*= 0*/)
{
	if (dest == NULL || nDestSize <= 0) return;

	int nCurrentIndex = nBeginIndex;
	for (int nIndex = 0; nIndex < nDestSize; nIndex++)
	{
		if (nCurrentIndex < 0 || nCurrentIndex >= MEMFILL_TABLE_MAX)
			nCurrentIndex = 0;

		dest[nIndex] = m_RepTable[nCurrentIndex];

		nCurrentIndex++;
	}
}

/*
함수: Set
역할: 기존 메모리를 삭제하고 신규 메모리를 생성, 복사한다.
인자: pData (신규 메모리에 복사할 메모리)
	nSize (복사할 메모리 사이즈)
반환: 
*/
void CByteMemory::Set(const byte* pData, int nSize)
{
	Clear();
	m_pBuffer = (byte*)malloc(nSize);
	memcpy(m_pBuffer, pData, nSize);
	m_nSize = nSize;
}

/*
함수: Set
역할: 기존 메모리를 삭제하고 신규 메모리를 생성, 복사한다.
인자: nCount (가변 인자의 개수)
	가변인자 형식: size + byte, size + const byte*
	size 가 1일 경우 byte 로 취급합니다.
반환:
*/
void CByteMemory::Set(int nCount, ...)
{
	Clear();

	va_list args;

	int nFullSize = 0;
	int nSize = 0;

	//
	va_start(args, nCount);
	for (int nLoop = 0; nLoop < nCount; nLoop += 2)
	{
		nSize = va_arg(args, int);
		nFullSize += nSize;
		if (nSize == 1)
			va_arg(args, byte);
		else
			va_arg(args, const byte*);
	}
	va_end(args);

	//
	m_pBuffer = (byte*)malloc(nFullSize);

	//
	int nPos = 0;
	va_start(args, nCount);
	for (int nLoop = 0; nLoop < nCount; nLoop += 2)
	{
		nSize = va_arg(args, int);
		if (nSize == 1)
			m_pBuffer[nPos] = va_arg(args, byte);
		else
			memcpy(&m_pBuffer[nPos], va_arg(args, const byte*), nSize);

		nPos += nSize;
	}
	va_end(args);

	m_nSize = nFullSize;
}

/*
함수: Add
역할: 기존 메모리에 메모리를 추가한다.
인자: pData (추가할 메모리)
	nSize (추가할 메모리 사이즈)
반환: 추가 후 메모리 사이즈
*/
int CByteMemory::Add(const byte* pData, int nSize)
{
	if (nSize <= 0) {
		return m_nSize;
	}

	if (m_nSize <= 0) {
		Set(pData, nSize);
		return m_nSize;
	}

	//
	int nNewSize = m_nSize + nSize;
	byte* pNewBuffer = (byte*)malloc(nNewSize);
	memcpy(pNewBuffer, m_pBuffer, m_nSize);
	memcpy(pNewBuffer + m_nSize, pData, nSize);

	//
	Clear();

	m_nSize = nNewSize;
	m_pBuffer = pNewBuffer;

	return m_nSize;
}

/*
함수: Add
역할: 특정 byte 값을 기존 메모리에 추가한다.
인자: data (특정 byte 값)
반환: 추가 후 메모리 사이즈
*/
int CByteMemory::Add(byte data)
{
	return  Add(&data, 1);
}

/*
함수: TakeIt
역할: 기존 메모리를 신규 생성하여 반환한다.
인자: pSize (반환되는 메모리의 사이즈)
반환: 신규 생성된 메모리
*/
byte* CByteMemory::TakeIt(int* pSize /*= NULL*/)
{
	if (m_nSize <= 0) {
		if (pSize != NULL)
			*pSize = 0;
		return 0;
	}

	byte* pNew = (byte*)malloc(m_nSize);
	memcpy(pNew, m_pBuffer, m_nSize);
	if (pSize != NULL)
		*pSize = m_nSize;
	return pNew;
}

/*
함수: MemSet
역할: 기존 메모리의 값을 특정 값으로 초기화한다.
인자: c (특정 값)
반환:
*/
void CByteMemory::MemSet(byte c)
{
	if (m_nSize <= 0) {
		return;
	}

	memset(m_pBuffer, c, m_nSize);
}

/*
함수: Create
역할: 특정 사이즈로 메모리 신규 생성
인자: nNewSize (생성할 메모리 사이즈)
	init (초기화할 값)
반환: true (생성 성공)
*/
bool CByteMemory::Create(int nNewSize, byte init /*= 0x00*/)
{
	if (nNewSize <= 0) {
		return false;
	}

	Clear();
	m_pBuffer = (byte*)malloc(nNewSize);
	memset(m_pBuffer, init, nNewSize);
	m_nSize = nNewSize;
	return true;
}

/*
함수: GetFirst
역할: 기존 메모리의 최초 값을 반환한다.
인자:
반환: 최초 byte 값
*/
byte CByteMemory::GetFirst()
{
	if (m_nSize < 0) return 0x00;
	return m_pBuffer[0];
}

/*
함수: GetLast
역할: 기존 메모리의 마지막 값을 반환한다.
인자: 
반환: 마지막 byte 값
*/
byte CByteMemory::GetLast()
{
	if (m_nSize <= 0) return 0x00;
	return m_pBuffer[m_nSize - 1];
}

/*
함수: CopyFrom
역할: CByteMemory 로 부터 메모리를 복사한다.
인자: pMemory(복사 원본 클래스)
반환: 복사 된 후 최종 메모리 사이즈
*/
int CByteMemory::CopyFrom(CByteMemory * pMemory)
{
	Clear();
	if (pMemory == NULL) return 0;

	return Add(pMemory->Get(), pMemory->Size());
}

/*
함수: CopyFrom
역할: CByteMemory 로 부터 주어진 사이즈만큼 메모리를 복사한다.
	만약 사이즈가 대상 메모리보다 크면 복사 실패
인자: pMemory(복사 원본 클래스)
	nSize (복사할 사이즈)
반환: true (복사 성공)
*/
bool CByteMemory::CopyFrom(CByteMemory * pMemory, int nSize)
{
	if (pMemory == NULL) return false;
	else if (m_nSize < nSize) return false;

	memcpy(m_pBuffer, pMemory->Get(), nSize);

	return true;
}

/*
함수: SetValue
역할: 기존 메모리의 특정 위치에 값을 설정한다.
인자: nPos (값을 설정할 위치)
	val (설정할 값)
반환: true (설정 성공)
*/
bool CByteMemory::SetValue(int nPos, byte val)
{
	if (nPos < 0) return false;
	else if (nPos >= m_nSize) return false;

	m_pBuffer[nPos] = val;
	return true;
}

/*
함수: SetLast
역할: 기존 메모리의 마지막 부분에 값을 설정한다.
인자: val (설정할 byte)
반환:
*/
bool CByteMemory::SetLast(byte val)
{
	return SetValue(m_nSize - 1, val);
}

/*
함수: operator+=
역할: 기존 메모리에 값을 추가한다.
인자: c (byte 값)
반환:
*/
void CByteMemory::operator+=(byte c)
{
	Add(c);
}

/* 
함수: GetBuffer
역할: 특정 위치부터의 메모리를 반환한다.
인자: nPos (특정 위치)
반환: 메모리
*/
const byte* CByteMemory::GetBuffer(int nPos)
{
	if (nPos < 0 || nPos >= m_nSize) return NULL;

	return &m_pBuffer[nPos];
}

/*
함수: SetBuffer
역할: 메모리 해당 위치에 복사를 한다.
인자: src (복사할 데이터)
	nPos (복사할 위치)
	nSize (복사할 데이터 사이즈)
반환: true(복사 성공)
*/
bool CByteMemory::SetBuffer(const byte* src, int nPos, int nSize)
{
	if (src == NULL || nPos < 0 || nSize <= 0) return false;
	else if (nPos + nSize - 1 > m_nSize) return false;

	memcpy(&m_pBuffer[nPos], src, nSize);
	return true;
}

/*
함수: MemCpy
역할: memcpy 를 진행한다.
인자: src (복사할 메모리)
	nSize (복사할 메모리 사이즈)
반환: true(복사 성공)
*/
bool CByteMemory::MemCpy(const byte * src, int nSize)
{
	if (src == NULL || nSize <= 0) return false;
	else if (m_nSize < nSize) return false;

	memcpy(m_pBuffer, src, nSize);
	return true;
}

/*
함수: ItLast
역할: 뒤에서 부터 주어진 위치의 메모리를 반환
인자: nFromLast (끝자리 부터의 인덱스)
반환: 메모리 반환 (실패시 NULL)
*/
byte * CByteMemory::ItLast(int nFromLast)
{
	if (nFromLast >= m_nSize || nFromLast < 0)
		return NULL;

	//
	return &m_pBuffer[(m_nSize - 1) - nFromLast];
}

