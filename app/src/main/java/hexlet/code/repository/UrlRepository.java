package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository{

    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";

        try (var connection = dataSource.getConnection();
            var statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                url.setId(keys.getLong(1));
                url.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
            }
        }
    }

    public static Optional<Url> find(long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            var result = statement.executeQuery();
            if (result.next()) {
                var url = new Url(result.getString("name"));
                url.setId(result.getLong("id"));
                url.setCreatedAt(result.getTimestamp("created_at"));
                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static List<Url> getEntities(long id) throws SQLException {
        var sql = "SELECT * FROM urls";

        try (var connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {
            var result = statement.executeQuery();

            var urls = new ArrayList<Url>();
            while (result.next()) {
                var url = new Url(result.getString("name"));
                url.setId(result.getLong("id"));
                url.setCreatedAt(result.getTimestamp("created_at"));
                urls.add(url);
            }
            return urls;
        }
    }
}
