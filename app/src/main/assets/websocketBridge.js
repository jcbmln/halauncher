var handleThemeUpdate = (event) => {
    event = event.data || event;
    var theme = event.default_theme;

    window.externalApp.externalBus(JSON.stringify({
        type: "frontend/get_themes", 
        themes: event.themes
    }));

    if (theme === "default") {
        window.externalApp.themesUpdated(JSON.stringify({
            name: theme
        }));
    } else {
        window.externalApp.themesUpdated(JSON.stringify({
            name: theme,
            styles: event.themes[theme]
        }));
    }
};

window.hassConnection.then(({conn}) => {
    conn.sendMessagePromise({
        type: "frontend/get_themes"
    }).then(handleThemeUpdate);
    conn.subscribeEvents(handleThemeUpdate, "themes_updated")
});
