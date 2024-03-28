<#macro page>
<html>
  <head>
    <title>${title}</title>
    <link rel="stylesheet" href="style.css"/>
    <meta charset="utf-8" />
  </head>
  <body>
    <nav>
      <ul>
        <li><a href="/train">Trains</a></li>
      </ul>
    </nav>

    <h1>${title}</h1>

    <#-- ici est inséré le contenu du sous template  -->
         <#nested>
  </body>
</html>
</#macro>
