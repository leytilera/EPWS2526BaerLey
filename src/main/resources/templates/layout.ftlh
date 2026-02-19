<#macro page title="ChessFed" scripts=[]>
    <!DOCTYPE html>

    <head>
        <meta charset="UTF-8">
        <title>ChessFed</title>
        <link rel="stylesheet" href="/styles/_combined.css" />
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <script src="/jocly/jocly.js"></script>
    </head>

    <body>
        <div class="app-shell--base">
            <#include "sidebar.ftlh">
                <main>
                    <#nested>
                </main>
        </div>

        <script type="module" src="/scripts/navigation.js"></script>
        <script type="module" src="/scripts/main.js"></script>

        <#list scripts as script>
            <script type="module" src="${script}"></script>
        </#list>
    </body>

    </html>
</#macro>