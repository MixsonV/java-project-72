@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@import hexlet.code.dto.UrlsPage
@import hexlet.code.model.Url;
@param UrlsPage page

@template.layout.page(
	page = page,
    content = @`
    	<div class="table-page">
			@if(page.getUrls().size() > 0)
				<p class="website">Сайты</p>
				<table>
					<thead>
						<tr>
							<th>ID</th>
							<th>Имя</th>
							<th>Последняя проверка</th>
							<th>Код ответа</th>
						</tr>
					</thead>
					<tbody>
						@for(Url url : page.getUrls())
							<tr>
								<td>${url.getId()}</td>
								<td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
								<td>
									${url.getLastCheck() != null ? url.getLastCheck().getCreatedAt()
									.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "Не проверен"}
								</td>
								<td>
									${url.getLastCheck() != null ?
									String.valueOf(url.getLastCheck().getStatusCode()) : "Нет кода статуса"}
								</td>
							</tr>
						@endfor
					</tbody>
				</table>
			@else
				<p>Нет добавленных сайтов</p>
			@endif
		</div>
    `
)