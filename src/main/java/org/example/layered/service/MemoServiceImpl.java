package org.example.layered.service;

import org.example.layered.dto.MemoRequestDto;
import org.example.layered.dto.MemoResponseDto;
import org.example.layered.entity.Memo;
import org.example.layered.repository.MemoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Annotation @Service는 @Component와 같다, Spring Bean으로 등록한다는 뜻.
 * Spring Bean으로 등록되면 다른 클래스에서 주입하여 사용할 수 있다.
 * 명시적으로 Service Layer 라는것을 나타낸다.
 * 비지니스 로직을 수행한다.
 */
@Service
public class MemoServiceImpl implements MemoService {

    private final MemoRepository memoRepository;

    public MemoServiceImpl(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    @Override
    public MemoResponseDto saveMemo(MemoRequestDto requestDto) {

        // 요청받은 데이터로 Memo 객체 생성
        Memo memo = new Memo(requestDto.getTitle(), requestDto.getContents());

        // 저장
        return memoRepository.saveMemo(memo);
    }


    @Override
    public List<MemoResponseDto> findAllMemos() {

        // 전체 조회
        // List<MemoResponseDto> allMemos = memoRepository.findAllMemos();

        return memoRepository.findAllMemos();
    }

    @Override
    public MemoResponseDto findMemoById(Long id) {
        // 식별자의 Memo가 없다면?
        Optional<Memo> optionalMemo = memoRepository.findMemoById(id);

        // NPE 방지
        if (optionalMemo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

        return new MemoResponseDto(optionalMemo.get());
    }

    @Transactional
    @Override
    public MemoResponseDto updateMemo(Long id, String title, String contents) {

        // 필수값 검증
        if (title == null || contents == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        // memo 수정
        int updatedRow = memoRepository.updateMemo(id, title, contents);
        // 수정된 row가 0개라면
        if (updatedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data has been modified.");
        }

        // 수정된 메모 조회
        return new MemoResponseDto(memoRepository.findMemoById(id).get());
    }

    @Transactional
    @Override
    public MemoResponseDto updateTitle(Long id, String title, String contents) {

        // 필수값 검증
        if (title == null || contents != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The title and content are required values.");
        }

        // memo 제목 수정
        int updatedRow = memoRepository.updateTitle(id, title);
        // 수정된 row가 0개 라면
        if (updatedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No data has been modified.");
        }

        // 수정된 메모 조회
        return new MemoResponseDto(memoRepository.findMemoById(id).get());
    }

    @Override
    public void deleteMemo(Long id) {
        // memo 삭제
        int deletedRow = memoRepository.deleteMemo(id);
        // 삭제된 row가 0개 라면
        if (deletedRow == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + id);
        }

    }
}