package hexlet.code.repository;

import hexlet.code.model.UrlCheck;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UrlCheckRepository extends BaseRepository {
    private static final int H1_INDEX = 3;
    private static final int TITLE_INDEX = 4;
    private static final int DESCRIPTION_INDEX = 5;
    private static final int CREATED_AT_INDEX = 6;

    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (var connection = dataSource.getConnection();
        var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, urlCheck.getUrlId());
            statement.setInt(2, urlCheck.getStatusCode());
            statement.setString(H1_INDEX, urlCheck.getH1());
            statement.setString(TITLE_INDEX, urlCheck.getTitle());
            statement.setString(DESCRIPTION_INDEX, urlCheck.getDescription());
            statement.setTimestamp(CREATED_AT_INDEX, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                urlCheck.setId(keys.getLong(1));
                urlCheck.setCreatedAt(LocalDateTime.now());
            }
        }
    }

    public static List<UrlCheck> findByUrlId(long urlId) throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY created_at DESC";
        try (var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, urlId);
            var result = statement.executeQuery();

            var checks = new ArrayList<UrlCheck>();
            while (result.next()) {
                var check = new UrlCheck(
                        result.getInt("status_code"),
                        result.getString("title"),
                        result.getString("h1"),
                        result.getString("description"),
                        result.getLong("url_id")
                );
                check.setId(result.getLong("id"));
                check.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
                checks.add(check);
            }
            return checks;
        }
    }

    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql = "SELECT * FROM url_checks WHERE id IN "
                + "(SELECT MAX(id) FROM url_checks GROUP BY url_id)";
        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();

            var checks = new HashMap<Long, UrlCheck>();
            while (result.next()) {
                var check = new UrlCheck(
                        result.getInt("status_code"),
                        result.getString("title"),
                        result.getString("h1"),
                        result.getString("description"),
                        result.getLong("url_id")
                );
                check.setId(result.getLong("id"));
                check.setCreatedAt(result.getTimestamp("created_at").toLocalDateTime());
                checks.put(check.getUrlId(), check);
                log.info("Found check: urlId={}, statusCode={}", result.getLong("url_id"),
                        result.getInt("status_code"));
            }
            log.info("Total checks found: {}", checks.size());
            return checks;
        }
    }
}
