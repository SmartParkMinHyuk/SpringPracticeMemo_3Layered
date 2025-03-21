package org.example.layered.repository;

import org.example.layered.dto.MemoRequestDto;
import org.example.layered.dto.MemoResponseDto;
import org.example.layered.entity.Memo;

import java.util.List;

public interface MemoRepository {

    Memo saveMemo(Memo memo);

    List<MemoResponseDto> findAllMemos();

    Memo findMemoById(Long id);

    void deleteMemo(Long id);
}

