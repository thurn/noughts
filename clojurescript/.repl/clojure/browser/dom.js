// Compiled by ClojureScript 0.0-2311
goog.provide('clojure.browser.dom');
goog.require('cljs.core');
goog.require('goog.object');
goog.require('goog.object');
goog.require('goog.dom');
goog.require('goog.dom');
/**
* @param {...*} var_args
*/
clojure.browser.dom.append = (function() { 
var append__delegate = function (parent,children){cljs.core.apply.call(null,goog.dom.append,parent,children);
return parent;
};
var append = function (parent,var_args){
var children = null;if (arguments.length > 1) {
  children = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return append__delegate.call(this,parent,children);};
append.cljs$lang$maxFixedArity = 1;
append.cljs$lang$applyTo = (function (arglist__15665){
var parent = cljs.core.first(arglist__15665);
var children = cljs.core.rest(arglist__15665);
return append__delegate(parent,children);
});
append.cljs$core$IFn$_invoke$arity$variadic = append__delegate;
return append;
})()
;
clojure.browser.dom.DOMBuilder = (function (){var obj15667 = {};return obj15667;
})();
clojure.browser.dom._element = (function() {
var _element = null;
var _element__1 = (function (this$){if((function (){var and__7369__auto__ = this$;if(and__7369__auto__)
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$1;
} else
{return and__7369__auto__;
}
})())
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$1(this$);
} else
{var x__8008__auto__ = (((this$ == null))?null:this$);return (function (){var or__7381__auto__ = (clojure.browser.dom._element[goog.typeOf(x__8008__auto__)]);if(or__7381__auto__)
{return or__7381__auto__;
} else
{var or__7381__auto____$1 = (clojure.browser.dom._element["_"]);if(or__7381__auto____$1)
{return or__7381__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"DOMBuilder.-element",this$);
}
}
})().call(null,this$);
}
});
var _element__2 = (function (this$,attrs_or_children){if((function (){var and__7369__auto__ = this$;if(and__7369__auto__)
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$2;
} else
{return and__7369__auto__;
}
})())
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$2(this$,attrs_or_children);
} else
{var x__8008__auto__ = (((this$ == null))?null:this$);return (function (){var or__7381__auto__ = (clojure.browser.dom._element[goog.typeOf(x__8008__auto__)]);if(or__7381__auto__)
{return or__7381__auto__;
} else
{var or__7381__auto____$1 = (clojure.browser.dom._element["_"]);if(or__7381__auto____$1)
{return or__7381__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"DOMBuilder.-element",this$);
}
}
})().call(null,this$,attrs_or_children);
}
});
var _element__3 = (function (this$,attrs,children){if((function (){var and__7369__auto__ = this$;if(and__7369__auto__)
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$3;
} else
{return and__7369__auto__;
}
})())
{return this$.clojure$browser$dom$DOMBuilder$_element$arity$3(this$,attrs,children);
} else
{var x__8008__auto__ = (((this$ == null))?null:this$);return (function (){var or__7381__auto__ = (clojure.browser.dom._element[goog.typeOf(x__8008__auto__)]);if(or__7381__auto__)
{return or__7381__auto__;
} else
{var or__7381__auto____$1 = (clojure.browser.dom._element["_"]);if(or__7381__auto____$1)
{return or__7381__auto____$1;
} else
{throw cljs.core.missing_protocol.call(null,"DOMBuilder.-element",this$);
}
}
})().call(null,this$,attrs,children);
}
});
_element = function(this$,attrs,children){
switch(arguments.length){
case 1:
return _element__1.call(this,this$);
case 2:
return _element__2.call(this,this$,attrs);
case 3:
return _element__3.call(this,this$,attrs,children);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
_element.cljs$core$IFn$_invoke$arity$1 = _element__1;
_element.cljs$core$IFn$_invoke$arity$2 = _element__2;
_element.cljs$core$IFn$_invoke$arity$3 = _element__3;
return _element;
})()
;
/**
* @param {...*} var_args
*/
clojure.browser.dom.log = (function() { 
var log__delegate = function (args){return console.log(cljs.core.apply.call(null,cljs.core.pr_str,args));
};
var log = function (var_args){
var args = null;if (arguments.length > 0) {
  args = cljs.core.array_seq(Array.prototype.slice.call(arguments, 0),0);} 
return log__delegate.call(this,args);};
log.cljs$lang$maxFixedArity = 0;
log.cljs$lang$applyTo = (function (arglist__15668){
var args = cljs.core.seq(arglist__15668);
return log__delegate(args);
});
log.cljs$core$IFn$_invoke$arity$variadic = log__delegate;
return log;
})()
;
clojure.browser.dom.log_obj = (function log_obj(obj){return console.log(obj);
});
Element.prototype.clojure$browser$dom$DOMBuilder$ = true;
Element.prototype.clojure$browser$dom$DOMBuilder$_element$arity$1 = (function (this$){var this$__$1 = this;clojure.browser.dom.log.call(null,"js/Element (-element ",this$__$1,")");
return this$__$1;
});
cljs.core.PersistentVector.prototype.clojure$browser$dom$DOMBuilder$ = true;
cljs.core.PersistentVector.prototype.clojure$browser$dom$DOMBuilder$_element$arity$1 = (function (this$){var this$__$1 = this;clojure.browser.dom.log.call(null,"PersistentVector (-element ",this$__$1,")");
var tag = cljs.core.first.call(null,this$__$1);var attrs = cljs.core.second.call(null,this$__$1);var children = cljs.core.drop.call(null,(2),this$__$1);if(cljs.core.map_QMARK_.call(null,attrs))
{return clojure.browser.dom._element.call(null,tag,attrs,children);
} else
{return clojure.browser.dom._element.call(null,tag,null,cljs.core.rest.call(null,this$__$1));
}
});
(clojure.browser.dom.DOMBuilder["string"] = true);
(clojure.browser.dom._element["string"] = (function() {
var G__15676 = null;
var G__15676__1 = (function (this$){clojure.browser.dom.log.call(null,"string (-element ",this$,")");
if((this$ instanceof cljs.core.Keyword))
{return goog.dom.createElement(cljs.core.name.call(null,this$));
} else
{return goog.dom.createTextNode(cljs.core.name.call(null,this$));

}
});
var G__15676__2 = (function (this$,attrs_or_children){clojure.browser.dom.log.call(null,"string (-element ",this$," ",attrs_or_children,")");
var attrs = cljs.core.first.call(null,attrs_or_children);if(cljs.core.map_QMARK_.call(null,attrs))
{return clojure.browser.dom._element.call(null,this$,attrs,cljs.core.rest.call(null,attrs_or_children));
} else
{return clojure.browser.dom._element.call(null,this$,null,attrs_or_children);
}
});
var G__15676__3 = (function (this$,attrs,children){clojure.browser.dom.log.call(null,"string (-element ",this$," ",attrs," ",children,")");
var str_attrs = (((cljs.core.map_QMARK_.call(null,attrs)) && (cljs.core.seq.call(null,attrs)))?cljs.core.reduce.call(null,(function (o,p__15669){var vec__15670 = p__15669;var k = cljs.core.nth.call(null,vec__15670,(0),null);var v = cljs.core.nth.call(null,vec__15670,(1),null);var o__$1 = (((o == null))?(function (){var obj15672 = {};return obj15672;
})():o);clojure.browser.dom.log.call(null,"o = ",o__$1);
clojure.browser.dom.log.call(null,"k = ",k);
clojure.browser.dom.log.call(null,"v = ",v);
if(((k instanceof cljs.core.Keyword)) || (typeof k === 'string'))
{var G__15673 = o__$1;(G__15673[cljs.core.name.call(null,k)] = v);
return G__15673;
} else
{return null;
}
}),(function (){var obj15675 = {};return obj15675;
})(),attrs):null);clojure.browser.dom.log_obj.call(null,str_attrs);
if(cljs.core.seq.call(null,children))
{return cljs.core.apply.call(null,goog.dom.createDom,cljs.core.name.call(null,this$),str_attrs,cljs.core.map.call(null,clojure.browser.dom._element,children));
} else
{return goog.dom.createDom(cljs.core.name.call(null,this$),str_attrs);
}
});
G__15676 = function(this$,attrs,children){
switch(arguments.length){
case 1:
return G__15676__1.call(this,this$);
case 2:
return G__15676__2.call(this,this$,attrs);
case 3:
return G__15676__3.call(this,this$,attrs,children);
}
throw(new Error('Invalid arity: ' + arguments.length));
};
G__15676.cljs$core$IFn$_invoke$arity$1 = G__15676__1;
G__15676.cljs$core$IFn$_invoke$arity$2 = G__15676__2;
G__15676.cljs$core$IFn$_invoke$arity$3 = G__15676__3;
return G__15676;
})()
);
/**
* @param {...*} var_args
*/
clojure.browser.dom.element = (function() {
var element = null;
var element__1 = (function (tag_or_text){clojure.browser.dom.log.call(null,"(element ",tag_or_text,")");
return clojure.browser.dom._element.call(null,tag_or_text);
});
var element__2 = (function() { 
var G__15677__delegate = function (tag,children){clojure.browser.dom.log.call(null,"(element ",tag," ",children,")");
var attrs = cljs.core.first.call(null,children);if(cljs.core.map_QMARK_.call(null,attrs))
{return clojure.browser.dom._element.call(null,tag,attrs,cljs.core.rest.call(null,children));
} else
{return clojure.browser.dom._element.call(null,tag,null,children);
}
};
var G__15677 = function (tag,var_args){
var children = null;if (arguments.length > 1) {
  children = cljs.core.array_seq(Array.prototype.slice.call(arguments, 1),0);} 
return G__15677__delegate.call(this,tag,children);};
G__15677.cljs$lang$maxFixedArity = 1;
G__15677.cljs$lang$applyTo = (function (arglist__15678){
var tag = cljs.core.first(arglist__15678);
var children = cljs.core.rest(arglist__15678);
return G__15677__delegate(tag,children);
});
G__15677.cljs$core$IFn$_invoke$arity$variadic = G__15677__delegate;
return G__15677;
})()
;
element = function(tag,var_args){
var children = var_args;
switch(arguments.length){
case 1:
return element__1.call(this,tag);
default:
return element__2.cljs$core$IFn$_invoke$arity$variadic(tag, cljs.core.array_seq(arguments, 1));
}
throw(new Error('Invalid arity: ' + arguments.length));
};
element.cljs$lang$maxFixedArity = 1;
element.cljs$lang$applyTo = element__2.cljs$lang$applyTo;
element.cljs$core$IFn$_invoke$arity$1 = element__1;
element.cljs$core$IFn$_invoke$arity$variadic = element__2.cljs$core$IFn$_invoke$arity$variadic;
return element;
})()
;
/**
* Remove all children from the element with the passed id.
*/
clojure.browser.dom.remove_children = (function remove_children(id){var parent = goog.dom.getElement(cljs.core.name.call(null,id));return goog.dom.removeChildren(parent);
});
clojure.browser.dom.get_element = (function get_element(id){return goog.dom.getElement(cljs.core.name.call(null,id));
});
clojure.browser.dom.html__GT_dom = (function html__GT_dom(s){return goog.dom.htmlToDocumentFragment(s);
});
clojure.browser.dom.insert_at = (function insert_at(parent,child,index){return goog.dom.insertChildAt(parent,child,index);
});
/**
* Coerce the argument to a dom element if possible.
*/
clojure.browser.dom.ensure_element = (function ensure_element(e){if((e instanceof cljs.core.Keyword))
{return clojure.browser.dom.get_element.call(null,e);
} else
{if(typeof e === 'string')
{return clojure.browser.dom.html__GT_dom.call(null,e);
} else
{return e;

}
}
});
/**
* Replace old-node with new-node. old-node can be an element or a
* keyword which is the id of the node to replace.  new-node can be an
* element or an html string.
*/
clojure.browser.dom.replace_node = (function replace_node(old_node,new_node){var old_node__$1 = clojure.browser.dom.ensure_element.call(null,old_node);var new_node__$1 = clojure.browser.dom.ensure_element.call(null,new_node);goog.dom.replaceNode(new_node__$1,old_node__$1);
return new_node__$1;
});
/**
* Set the text content for the passed element returning the
* element. If a keyword is passed in the place of e, the element with
* that id will be used and returned.
*/
clojure.browser.dom.set_text = (function set_text(e,s){return goog.dom.setTextContent(clojure.browser.dom.ensure_element.call(null,e),s);
});
/**
* Get the value of an element.
*/
clojure.browser.dom.get_value = (function get_value(e){return clojure.browser.dom.ensure_element.call(null,e).value;
});
/**
* Set properties on an element
*/
clojure.browser.dom.set_properties = (function set_properties(e,m){return goog.dom.setProperties(clojure.browser.dom.ensure_element.call(null,e),cljs.core.apply.call(null,goog.object.create,cljs.core.interleave.call(null,cljs.core.keys.call(null,m),cljs.core.vals.call(null,m))));
});
/**
* Set the value property for an element.
*/
clojure.browser.dom.set_value = (function set_value(e,v){return clojure.browser.dom.set_properties.call(null,e,new cljs.core.PersistentArrayMap(null, 1, ["value",v], null));
});
clojure.browser.dom.click_element = (function click_element(e){return clojure.browser.dom.ensure_element.call(null,e).click(cljs.core.List.EMPTY);
});
