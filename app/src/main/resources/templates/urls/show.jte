@import hexlet.code.model.Url
@param Url url
@param String flash
@param String flashType

@template.layout(content = @`
    <h1 class="mb-4">${url.getName()}</h1>
    <table data-test="url" class="table table-bordered">
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
            <td>${url.getCreatedAt() != null ? url.getCreatedAt().toLocalDateTime().toLocalDate().toString() : ""}</td>
        </tr>
        </tbody>
    </table>
`, flash = flash, flashType = flashType)