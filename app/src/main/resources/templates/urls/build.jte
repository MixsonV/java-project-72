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
		   <p>
			   <h1>Анализатор страниц</h1>
			   <h3>Бесплатно проверяйте сайты на SEO пригодность</h3>
		   </p>
		   <label>
			  <input type="text" required name="url" placeholder="ссылка"/>
		   </label>
			  <input type="submit" value="Проверить"/>
		   <p>
			  Пример: https://www.example.com
		   </p>
	   </form>
    `,
    footer = @`
        <p>
            <a href="${NamedRoutes.urlsPath()}">К списку страниц</a>
        </p>
    `
)