package org.example.layered.repository;

import org.example.layered.dto.MemoResponseDto;
import org.example.layered.entity.Memo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcTemplateMemoRepository implements MemoRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemoRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public MemoResponseDto saveMemo(Memo memo) {
        // INSERT Query를 직접 작성하지 않아도 된다.
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("memo").usingGeneratedKeyColumns("id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", memo.getTitle());
        parameters.put("contents", memo.getContents());

        // 저장 후 생성된 key값을 Number 타입으로 반환하는 메서드
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));

        return new MemoResponseDto(key.longValue(), memo.getTitle(), memo.getContents());
    }

    @Override
    public List<MemoResponseDto> findAllMemos() {
        return jdbcTemplate.query("select * from memo", memoRowMapper());
    }

    @Override
    public Optional<Memo> findMemoById(Long id) {
        List<Memo> result = jdbcTemplate.query("select * from memo where id = ?", memoRowMapperV2(), id);

        // 만약 null 값이 들어가 있을 수 있기 때문에 optional 형태로 만든다.
        return result.stream().findAny();
    }


    @Override
    public int deleteMemo(Long id) {
        // 쿼리의 영향을 받은 row 수를 int로 반환한다.
        return jdbcTemplate.update("delete from memo where id = ?", id);
    }

    @Override
    public int updateMemo(Long id, String title, String contents) {
        // 쿼리의 영향을 받은 row 수를 int로 반환한다.
        return jdbcTemplate.update("update memo set title = ?, contents = ? where id = ?", title, contents, id);
    }

    @Override
    public int updateTitle(Long id, String title) {
        return jdbcTemplate.update("update memo set title = ? where id = ?", title, id);
    }

    private RowMapper<MemoResponseDto> memoRowMapper() {
        return new RowMapper<MemoResponseDto>() {
            @Override
            public MemoResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new MemoResponseDto(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents")
                );
            }

        };
    }

    private RowMapper<Memo> memoRowMapperV2() {
        return new RowMapper<Memo>() {
            @Override
            public Memo mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Memo(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getString("contents")
                );
            }

        };
    }
}

