@import hexlet.code.dto.UrlPage
@import java.time.format.DateTimeFormatter
@import hexlet.code.util.NamedRoutes

@param UrlPage page

@template.layout.page(
	content = @`
		<div>ID: ${page.getUrl().getId()}</div>
		<div>Name: ${page.getUrl().getName()}</div>
		<div>Created At: ${page.getUrl().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}</div>
    `,
    footer = @`
    	<p>
			<a href="${NamedRoutes.urlsPath()}">Возврат к списку страниц</a>
		</p>
		<p>
		   <a href="${NamedRoutes.rootPath()}">Возврат на главную страницу</a>
		</p>
    `
)