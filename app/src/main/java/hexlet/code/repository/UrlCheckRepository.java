package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                urlCheck.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
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
                check.setCreatedAt(result.getTimestamp("created_at"));
                checks.add(check);
            }
            return checks;
        }
    }

    public static Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        var sql = "SELECT DISTINCT ON (url_id) * FROM url_checks ORDER BY url_id, created_at DESC";
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
                check.setCreatedAt(result.getTimestamp("created_at"));
                checks.put(check.getUrlId(), check);
            }
            return checks;
        }
    }
}
