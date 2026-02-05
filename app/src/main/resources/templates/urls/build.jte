@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.BuildUrlPage
@param BuildUrlPage page

@template.layout.page(
	page = page,
    content = @`
		@if(page.getErrors() != null)
			<div class="mb-3">
				<ul>
					@for(var validator : page.getErrors().values())
						@for(var error : validator)
							<li>${error.getMessage()}</li>
						@endfor
					@endfor
				</ul>
			</div>
		@endif

		<form action="${NamedRoutes.urlsPath()}" method="post">
			<div class="box-main-page">
				<p class="title">Анализатор страниц</p>
				<p class="description">Бесплатно проверяйте сайты на SEO пригодность</p>

				<label>
					<input class="url" type="text" required name="url" placeholder="ссылка"/>
				</label>

				<input class="btn1" type="submit" value="Проверить"/>
				<p class="example">Пример: https://www.example.com</p>
			</div>
	   </form>
    `
)