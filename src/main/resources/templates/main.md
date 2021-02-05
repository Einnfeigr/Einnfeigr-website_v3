{{#partial "dependencies"}}
	<link rel="stylesheet" href="/css/main.css" />
{{/partial}}

{{#partial "content" }}
	<h1>{{i18n "h1" bundle="text/main" locale=locale}}</h1>
	<h2>{{i18n "h2" bundle="text/main" locale=locale}}</h2>
	<p>
		{{i18n "welcome" bundle="text/main" locale=locale}}
	</p>
	<p>
		{{i18n "tech" bundle="text/main" locale=locale}}
		<a href="/about">{{i18n "techlink" bundle="text/main" locale=locale}}</a>
	</p>
{{/partial}}
{{>basis}}