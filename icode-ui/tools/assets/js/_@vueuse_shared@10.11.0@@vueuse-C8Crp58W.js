import{o as e,q as t,r as n,u as o,v as s,g as r,w as i}from"./_@vue_reactivity@3.4.29@@vue-Ujt3kFWg.js";import{p as u,n as a,j as c,w as f}from"./_@vue_runtime-core@3.4.29@@vue-DDZ-_xUj.js";function l(e){return!!r()&&(i(e),!0)}function p(e){return"function"==typeof e?e():o(e)}const v="undefined"!=typeof window&&"undefined"!=typeof document;"undefined"!=typeof WorkerGlobalScope&&(globalThis,WorkerGlobalScope);const m=Object.prototype.toString,d=e=>"[object Object]"===m.call(e),y=()=>{};const b=e=>e();function g(...o){if(1!==o.length)return e(...o);const r=o[0];return"function"==typeof r?t(s((()=>({get:r,set:y})))):n(r)}function h(e,t,n={}){const{eventFilter:o=b,...s}=n;return f(e,(r=o,i=t,function(...e){return new Promise(((t,n)=>{Promise.resolve(r((()=>i.apply(this,e)),{fn:i,thisArg:this,args:e})).then(t).catch(n)}))}),s);var r,i}function j(e,o,s={}){const{eventFilter:r,...i}=s,{eventFilter:u,pause:a,resume:c,isActive:f}=function(e=b){const o=n(!0);return{isActive:t(o),pause:function(){o.value=!1},resume:function(){o.value=!0},eventFilter:(...t)=>{o.value&&e(...t)}}}(r);return{stop:h(e,o,{...i,eventFilter:u}),pause:a,resume:c,isActive:f}}function w(e,t=!0,n){c()?u(e,n):t?e():a(e)}export{w as a,p as b,d as c,l as d,v as i,y as n,g as t,j as w};
