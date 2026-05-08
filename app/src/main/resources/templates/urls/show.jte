@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import java.util.List
@param Url url
@param List<UrlCheck> checks
@param String flash
@param String flashType

@template.layout(content = @`
    <h1 class="mb-4">${url.getName()}</h1>

    <table data-test="url" class="table table-bordered mb-4">
        <tbody>
        <tr>
            <td>ID</td>
            <td>${url.getId()}</td>
        </tr>
        <tr>
            <td>Имя</td>
            <td>${url.getName()}</td>
        </tr>
        <tr>
            <td>Дата создания</td>
            <td>${url.getCreatedAt() != null ? url.getCreatedAt().toLocalDate().toString() : ""}</td>
        </tr>
        </tbody>
    </table>

    <form method="post" action="/urls/${url.getId()}/checks" class="mb-4">
        <input type="submit" class="btn btn-primary" value="Запустить проверку">
    </form>

    <table data-test="checks" class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Код ответа</th>
            <th>h1</th>
            <th>title</th>
            <th>description</th>
            <th>Дата создания</th>
        </tr>
        </thead>
        <tbody>
        @for(UrlCheck check : checks)
            <tr>
                <td>${check.getId()}</td>
                <td>${check.getStatusCode()}</td>
                <td>${check.getH1() != null ? check.getH1() : ""}</td>
                <td>${check.getTitle() != null ? check.getTitle() : ""}</td>
                <td>${check.getDescription() != null ? check.getDescription() : ""}</td>
                <td>${check.getCreatedAt() != null ? check.getCreatedAt().toLocalDate().toString() : ""}</td>
            </tr>
        @endfor
        </tbody>
    </table>
`, flash = flash, flashType = flashType)