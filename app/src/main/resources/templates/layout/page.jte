@import gg.jte.Content
@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.BasePage
@param Content content
@param BasePage page = null

<style>
	.half-screen {
		margin-left: 0px;
		background-color: #303030;
		height: 5%;
	}

	nav a {
		margin-right: 20px;
		color: white;
	}

	nav .page {
		margin-left: 20px;
		font-size: 20px;
		font-weight: bold;
	}

	nav .main {
		font-size: 16px;
	}

	nav .web {
		font-size: 16px;
	}

	.message {
		margin-top: 0px;
		margin-left: 0px;
		margin-bottom: 0px;
		color: #072C63;
		background-color: #909590;
		height: 40px
	}

	body, html {
		background-color: #4e5d94;
	}

	.box-main-page {
		padding: 0% 2% 10% 2%;
		background-color: #4e5d94;
		color: white;

		p.title {
			font-size: 100px;
			font-weight: bold;
		}

		p.description {
			font-size: 20px;
			font-weight: bold;
		}

		p.example {
			font-size: 15px;
		}

		label {
			width: 75%;
		}

		input.url {
			height: 50px;
			font-size: 30px;
			background-color: white;
			width: 100%;
		}

		input.btn1 {
			height: 50px;
			width: 20%;
			font-size: 30px;
			background-color: #7289da;
		}
	}

	.table-page {
		padding: 0% 2% 10% 2%;
		background-color: #2b5b3366;
		color: black;
		font-size: 20px;

		table {
			width: 100%;
			border-collapse: collapse;
		}

		table, th, td {
			border: 1px solid black;
		}

		th, td {
			padding: 10px;
			text-align: left;
		}

		.website {
			font-size: 45px;
			font-weight: bold;
		}

		.btn2 {
			height: 50px;
			background-color: #57cd6a;
		}
	}
</style>

<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Java-Project-72</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
              crossorigin="anonymous">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
                crossorigin="anonymous"></script>
    </head>
    <body>
    	<header class="half-screen">
			<nav>
				<a href="${NamedRoutes.rootPath()}" class="page">Анализатор страниц</a>
				<a href="${NamedRoutes.rootPath()}" class="main">Главная страница</a>
				<a href="${NamedRoutes.urlsPath()}" class="web">Список сайтов</a>
			</nav>
		</header>

		<main>
			@if(page != null && page.getFlash() != null)
				<p>${page.getFlash()}</p>
			@endif
			${content}
		</main>
    </body>
</html>