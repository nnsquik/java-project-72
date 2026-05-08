@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import java.util.List
@import java.util.Map
@param List<Url> urls
@param Map<Long, UrlCheck> latestChecks
@param String flash
@param String flashType

@template.layout(content = @`
    <h1 class="mb-4">Сайты</h1>
    <table data-test="urls" class="table table-bordered">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Дата последней проверки</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @for(Url url : urls)
            <tr>
                <td>${url.getId()}</td>
                <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                <td>${latestChecks.containsKey(url.getId()) ? latestChecks.get(url.getId()).getCreatedAt().withNano(0).toString().replace("T", " ") : ""}</td>
                <td>${latestChecks.containsKey(url.getId()) ? String.valueOf(latestChecks.get(url.getId()).getStatusCode()) : ""}</td>
            </tr>
        @endfor
        </tbody>
    </table>
`, flash = flash, flashType = flashType)