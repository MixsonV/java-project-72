package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlCheckRepository extends BaseRepository {
    private static final String ID = "id";
    private static final String URL_ID = "url_id";
    private static final String STATUS_CODE = "status_code";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String CREATED_AT = "created_at";
    private static final String H1 = "h1";
    public static void saveUrlCheck(UrlCheck urlCheck) throws SQLException {
        String sql = "INSERT INTO url_checks "
                + "(url_id, status_code, title, h1, description, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, urlCheck.getUrlId());
            preparedStatement.setInt(2, urlCheck.getStatusCode());
            preparedStatement.setString(3, urlCheck.getTitle());
            preparedStatement.setString(4, urlCheck.getH1());
            preparedStatement.setString(5, urlCheck.getDescription());
            var createdAt = LocalDateTime.now();
            preparedStatement.setTimestamp(6, Timestamp.valueOf(createdAt));

            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(createdAt);
            } else {
                log.info("DB have not returned an id after saving an entity");
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> findAllChecksByUrlId(Long urlId) throws SQLException {
        List<UrlCheck> checks = new ArrayList<>();
        String sql =
                "SELECT id, url_id, status_code, title, h1, description, created_at FROM url_checks WHERE url_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UrlCheck check = new UrlCheck();
                check.setId(rs.getLong(ID));
                check.setUrlId(rs.getLong(URL_ID));
                check.setStatusCode(rs.getInt(STATUS_CODE));
                check.setTitle(rs.getString(TITLE));
                check.setH1(rs.getString(H1));
                check.setDescription(rs.getString(DESCRIPTION));
                check.setCreatedAt(rs.getTimestamp(CREATED_AT).toLocalDateTime());

                checks.add(check);
            }
        }
        return checks;
    }

    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) id, url_id, status_code, title, h1, description, created_at"
                + " from url_checks order by url_id DESC, id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            var resultSet = stmt.executeQuery();
            var result = new HashMap<Long, UrlCheck>();
            while (resultSet.next()) {
                var id = resultSet.getLong(ID);
                var urlId = resultSet.getLong(URL_ID);
                var statusCode = resultSet.getInt(STATUS_CODE);
                var title = resultSet.getString(TITLE);
                var h1 = resultSet.getString(H1);
                var description = resultSet.getString(DESCRIPTION);
                var createdAt = resultSet.getTimestamp(CREATED_AT).toLocalDateTime();
                var check = new UrlCheck(statusCode, title, h1, description);
                check.setId(id);
                check.setUrlId(urlId);
                check.setCreatedAt(createdAt);
                result.put(urlId, check);
            }
            return result;
        }
    }
}
