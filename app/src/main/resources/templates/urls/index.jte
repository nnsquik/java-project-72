@import hexlet.code.model.Url
@import java.util.List
@param List<Url> urls
@param String flash
@param String flashType

@template.layout(content = @`
    <h1 class="mb-4">Сайты</h1>
    <table data-test="urls" class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Дата создания</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @for(Url url : urls)
            <tr>
                <td>${url.getId()}</td>
                <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                <td>${url.getCreatedAt() != null ? url.getCreatedAt().toLocalDateTime().toLocalDate().toString() : ""}</td>
                <td></td>
            </tr>
        @endfor
        </tbody>
    </table>
`, flash = flash, flashType = flashType)