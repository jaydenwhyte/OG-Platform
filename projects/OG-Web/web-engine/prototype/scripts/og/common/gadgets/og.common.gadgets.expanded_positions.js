/*
 * Copyright 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Please see distribution for license.
 */
$.register_module({
    name: 'og.common.gadgets.ExpandedPositions',
    dependencies: [],
    obj: function () {
        var prefix = 'og_expanded_positions_gadget_', counter = 1, loading_template;
        return function (config) {
            var gadget = this, alive = prefix + counter++;
            gadget.alive = function () {return $('.' + alive).length ? true : false;};
            gadget.resize = function () {gadget.load();};
            gadget.load = function () {
                var selector_position = config.selector + ' .og-js-gadgets-positions',
                    selector_trades = config.selector + ' .og-js-trades-table';
                $(config.selector).addClass(alive).html(loading_template({text: 'loading...'}));
                $.when(og.api.text({module: 'og.views.gadgets.expanded_positions'})).pipe(function (template) {
                    $(config.selector).html(template);
                    og.common.gadgets.positions($.extend({}, config, {selector: selector_position}));
                    og.common.gadgets.trades($.extend({}, config, {selector: selector_trades}));
                });
            };
            if (!config.child) og.common.gadgets.manager.register(gadget);
            if (loading_template) gadget.load(); else og.api.text({module: 'og.analytics.loading_tash'})
                .pipe(function (template) {loading_template = Handlebars.compile(template); gadget.load();});
        }
    }
});