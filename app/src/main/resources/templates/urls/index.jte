@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.UrlsPage
@import hexlet.code.model.Url;
@param UrlsPage page

@template.layout.page(
	page = page,
    content = @`
		@if(page.getUrls().size() > 0)
			@for(Url url : page.getUrls())
				<p>
					<a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
				</p>
			@endfor
		@else
			<p>Нет добавленных страниц</p>
		@endif
    `,
    footer = @`
		<p>
			<a href="${NamedRoutes.rootPath()}">Возврат на главную страницу</a>
		</p>
    `
)