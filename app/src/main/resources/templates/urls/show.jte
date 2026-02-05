@import hexlet.code.dto.UrlPage
@import java.time.format.DateTimeFormatter
@import hexlet.code.util.NamedRoutes

@param UrlPage page

@template.layout.page(
	content = @`
		<div class="table-page">
			<p class="website">Сайт: ${page.getUrl().getName()}</p>
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Имя</th>
						<th>Дата создания</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${page.getUrl().getId()}</td>
						<td>${page.getUrl().getName()}</td>
						<td>${page.getUrl().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td>
					</tr>
				</tbody>
			</table>
			<p class="website">Проверки</p>
			<form action="/urls/${page.getUrl().getId()}/checks" method="post">
				<button class="btn2" type="submit">Запустить проверку</button>
			</form>
			<table>
				<thead>
					<tr>
						<th>ID</th>
						<th>Код ответа</th>
						<th>Заголовок</th>
						<th>H1</th>
						<th>Описание</th>
						<th>Дата проверки</th>
					</tr>
				</thead>

				@for(var check : page.getUrlChecks())
					<tbody>
						<tr>
							<td>${check.getId()}</td>
							<td>${check.getStatusCode()}</td>
							<td>${check.getTitle()}</td>
							<td>${check.getH1()}</td>
							<td>${check.getDescription()}</td>
							<td>${check.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</td>
						</tr>
					</tbody>
				@endfor
			</table>
		</div>
    `
)