/*
 * Copyright 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * Please see distribution for license.
 */
$.register_module({
    name: 'og.analytics.url',
    dependencies: ['og.common.routes', 'og.api.rest'],
    obj: function () {
        var url, last_fingerprint = {}, last_object = {}, routes = og.common.routes,
            main_selector = '.OG-layout-analytics-center',
            panels = ['south', 'dock-north', 'dock-center', 'dock-south'];
        var go = function () {
            og.api.rest.compressor.put({content: last_object, dependencies: ['data']}).pipe(function (result) {
                var current = routes.current(),
                    hash = routes.hash(og.views[og.analytics.blotter ? 'blotter' : 'analytics2']
                        .rules.load_item, {data: result.data.data});
                if (current.hash === hash) return url.process(current.args);
                routes.go(hash);
            });
        };
        var context_items = function (cell) {
            var items = [];
            var position_edit = function () {
                var arr = cell.row_value.positionId.split('~'), id = arr[0] + '~' + arr[1];
                og.api.rest.blotter.positions.get({id: id}).pipe(function(data){
                    data.data.trade.uniqueId = id;
                    new og.blotter.Dialog({
                        details: data, portfolio:{name: id, id: id}, 
                        handler: function (data) {return og.api.rest.blotter.positions.put(data);}
                    });
                });
            };
            var trade_edit = function () {
                og.api.rest.blotter.trades.get({id: cell.row_value.tradeId}).pipe(function(data){
                    new og.blotter.Dialog({
                        details: data, portfolio:{name: cell.row_value.nodeId, id: cell.row_value.nodeId},
                        handler: function (data) {return og.api.rest.blotter.trades.put(data);}
                    });
                });
            };
            var trade_insert = function () {
                new og.blotter.Dialog({portfolio:{name: cell.row_value.nodeId, id: cell.row_value.nodeId}, 
                    handler: function (data) {og.api.rest.blotter.trades.put(data);}
                });
            };
            items.push({name: 'Insert', handler: trade_insert});
            if ((cell.type === "POSITION" && cell.row in og.analytics.grid.meta.nodes) || cell.type === "NODE") {
                return items;
            }
            if((cell.type === "OTC_TRADE" || cell.type === "FUNGIBLE_TRADE") && cell.row_value.tradeId){
                items.push({name: 'Edit', handler: trade_edit}); 
            } 
            else {
                items.push({name: 'Edit', handler: position_edit}); 
            }
            return items;
        };
        return url = {
            add: function (container, params, silent) {
                return (last_object[container] || (last_object[container] = [])).push(params), (!silent && go()), url;
            },
            clear_main: function () {
                if (og.analytics.grid) og.analytics.grid.kill();
                last_fingerprint.main = last_object.main = null;
                return url;
            },
            last: last_object,
            launch: function (params) {
                var win = window.open(), url = './gadget.ftl' + routes.prefix() + '/gadgetscontainer/';
                og.api.rest.compressor.put({content: [params]})
                    .pipe(function (result) {win.location.href = url + result.data.data;});
            },
            main: function (params) {
                url.clear_main();
                $(main_selector).html('requesting...');
                return (last_object.main = params), go(), url;
            },
            process: function (args, handler) {
                $.when(args.data ? og.api.rest.compressor.get({content: args.data, dependencies: ['data']}) : void 0)
                .then(function (result) {
                    var config = result ? result.data.data : {}, current_main;
                    panels.forEach(function (panel) {delete last_object[panel];});
                    if (config.main && last_fingerprint.main !== (current_main = JSON.stringify(config.main))) {
                        new og.analytics.Form2({callback: og.analytics.url.main, data: config.main});
                        if (og.analytics.grid) og.analytics.grid.kill();
                        last_object.main = JSON.parse(last_fingerprint.main = current_main);
                        og.analytics.grid = new og.analytics.Grid({
                            selector: main_selector, cellmenu: true, show_save: og.analytics.blotter,
                            source: $.extend({blotter: og.analytics.blotter}, last_object.main)
                        }).on('viewchange', function (view) {
                            url.main($.extend({}, og.analytics.grid.source, {type: view}));
                        }).on('fatal', url.clear_main);
                        if (og.analytics.blotter) og.analytics.grid.on('contextmenu', function (event, cell, col) {
                            if (cell) return og.common.util.ui.contextmenu({defaults: true, zindex: 4, 
                                items: context_items(cell)}, event, cell);
                        });
                    } else {
                        new og.analytics.Form2({callback: og.analytics.url.main, data: config.main});
                    }
                    if (!config.main) {new og.analytics.Form2({ callback: og.analytics.url.main });}
                    panels.forEach(function (panel) {
                        var gadgets = config[panel], new_gadgets = [];
                        if (!gadgets || !gadgets.length)
                            return (last_fingerprint[panel] = []), (last_object[panel] = []);
                        if (!last_fingerprint[panel]) last_fingerprint[panel] = [];
                        if (!last_object[panel]) last_object[panel] = [];
                        last_fingerprint[panel] = gadgets.map(function (gadget, index) {
                            delete gadget.fingerprint;
                            var fingerprint = JSON.stringify(gadget);
                            last_object[panel][index] = JSON.parse(fingerprint);
                            if (last_fingerprint[panel][index] === fingerprint) return fingerprint;
                            gadget.fingerprint = fingerprint;
                            new_gadgets.push(gadget);
                            if (typeof new_gadgets.add_index === 'undefined') new_gadgets.add_index = index;
                            return fingerprint;
                        });
                        if (new_gadgets.length) og.analytics.containers[panel]
                            .add(new_gadgets, new_gadgets.length === 1 ? new_gadgets.add_index : void 0);
                    });
                    panels.forEach(function (panel) {
                        if (og.analytics.containers[panel])
                            og.analytics.containers[panel].verify(last_fingerprint[panel]);
                    });
                    if (handler) handler();
                });
                return url;
            },
            remove: function (container, index, silent) {
                if (!last_fingerprint[container] || !last_fingerprint[container].length) return;
                last_fingerprint[container].splice(index, 1);
                last_object[container].splice(index, 1);
                if (!last_fingerprint[container].length) delete last_fingerprint[container];
                if (!last_object[container].length) delete last_object[container];
                return (!silent && go()), url;
            },
            swap: function (container, params, index) {
                return (last_object[container][index] = params), go(), url;
            }
        };
    }
});