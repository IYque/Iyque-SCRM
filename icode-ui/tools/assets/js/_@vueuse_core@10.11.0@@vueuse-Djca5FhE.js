import{t as e,a as t,i as n,b as r,w as a,n as o,c as i,d as s}from"./_@vueuse_shared@10.11.0@@vueuse-C8Crp58W.js";import{l as u,w as l,n as c,m as d,p as f,j as m}from"./_@vue_runtime-core@3.4.29@@vue-DDZ-_xUj.js";import{n as v,r as g}from"./_@vue_reactivity@3.4.29@@vue-Ujt3kFWg.js";function p(e){var t;const n=r(e);return null!=(t=null==n?void 0:n.$el)?t:n}const h=n?window:void 0;function y(...e){let t,n,a,u;if("string"==typeof e[0]||Array.isArray(e[0])?([n,a,u]=e,t=h):[t,n,a,u]=e,!t)return o;Array.isArray(n)||(n=[n]),Array.isArray(a)||(a=[a]);const c=[],d=()=>{c.forEach((e=>e())),c.length=0},f=l((()=>[p(t),r(u)]),(([e,t])=>{if(d(),!e)return;const r=i(t)?{...t}:t;c.push(...n.flatMap((t=>a.map((n=>((e,t,n,r)=>(e.addEventListener(t,n,r),()=>e.removeEventListener(t,n,r)))(e,t,n,r))))))}),{immediate:!0,flush:"post"}),m=()=>{f(),d()};return s(m),m}function w(e){const t=function(){const e=g(!1),t=m();return t&&f((()=>{e.value=!0}),t),e}();return u((()=>(t.value,Boolean(e()))))}const b="undefined"!=typeof globalThis?globalThis:"undefined"!=typeof window?window:"undefined"!=typeof global?global:"undefined"!=typeof self?self:{},S="__vueuse_ssr_handlers__",k=A();function A(){return S in b||(b[S]=b[S]||{}),b[S]}function C(e,t){return k[e]||t}const E={boolean:{read:e=>"true"===e,write:e=>String(e)},object:{read:e=>JSON.parse(e),write:e=>JSON.stringify(e)},number:{read:e=>Number.parseFloat(e),write:e=>String(e)},any:{read:e=>e,write:e=>String(e)},string:{read:e=>e,write:e=>String(e)},map:{read:e=>new Map(JSON.parse(e)),write:e=>JSON.stringify(Array.from(e.entries()))},set:{read:e=>new Set(JSON.parse(e)),write:e=>JSON.stringify(Array.from(e))},date:{read:e=>new Date(e),write:e=>e.toISOString()}},L="vueuse-storage";function _(e,n,o,i={}){var s;const{flush:u="pre",deep:l=!0,listenToStorageChanges:d=!0,writeDefaults:f=!0,mergeDefaults:m=!1,shallow:p,window:w=h,eventFilter:b,onError:S=(e=>{}),initOnMounted:k}=i,A=(p?v:g)("function"==typeof n?n():n);if(!o)try{o=C("getDefaultStorage",(()=>{var e;return null==(e=h)?void 0:e.localStorage}))()}catch(J){S(J)}if(!o)return A;const _=r(n),N=function(e){return null==e?"any":e instanceof Set?"set":e instanceof Map?"map":e instanceof Date?"date":"boolean"==typeof e?"boolean":"string"==typeof e?"string":"object"==typeof e?"object":Number.isNaN(e)?"any":"number"}(_),j=null!=(s=i.serializer)?s:E[N],{pause:O,resume:M}=a(A,(()=>function(t){try{const n=o.getItem(e);if(null==t)T(n,null),o.removeItem(e);else{const r=j.write(t);n!==r&&(o.setItem(e,r),T(n,r))}}catch(J){S(J)}}(A.value)),{flush:u,deep:l,eventFilter:b});function T(t,n){w&&w.dispatchEvent(new CustomEvent(L,{detail:{key:e,oldValue:t,newValue:n,storageArea:o}}))}function D(t){if(!t||t.storageArea===o)if(t&&null==t.key)A.value=_;else if(!t||t.key===e){O();try{(null==t?void 0:t.newValue)!==j.write(A.value)&&(A.value=function(t){const n=t?t.newValue:o.getItem(e);if(null==n)return f&&null!=_&&o.setItem(e,j.write(_)),_;if(!t&&m){const e=j.read(n);return"function"==typeof m?m(e,_):"object"!==N||Array.isArray(e)?e:{..._,...e}}return"string"!=typeof n?n:j.read(n)}(t))}catch(J){S(J)}finally{t?c(M):M()}}}function I(e){D(e.detail)}return w&&d&&t((()=>{y(w,"storage",D),y(w,L,I),k&&D()})),k||D(),A}function N(e){return function(e,t={}){const{window:n=h}=t,a=w((()=>n&&"matchMedia"in n&&"function"==typeof n.matchMedia));let o;const i=g(!1),u=e=>{i.value=e.matches},l=()=>{o&&("removeEventListener"in o?o.removeEventListener("change",u):o.removeListener(u))},c=d((()=>{a.value&&(l(),o=n.matchMedia(r(e)),"addEventListener"in o?o.addEventListener("change",u):o.addListener(u),i.value=o.matches)}));return s((()=>{c(),l(),o=void 0})),i}("(prefers-color-scheme: dark)",e)}function j(n={}){const{valueDark:r="dark",valueLight:a="",window:o=h}=n,i=function(n={}){const{selector:r="html",attribute:a="class",initialValue:o="auto",window:i=h,storage:s,storageKey:c="vueuse-color-scheme",listenToStorageChanges:d=!0,storageRef:f,emitAuto:m,disableTransition:v=!0}=n,g={auto:"",light:"light",dark:"dark",...n.modes||{}},y=N({window:i}),w=u((()=>y.value?"dark":"light")),b=f||(null==c?e(o):_(c,o,s,{window:i,listenToStorageChanges:d})),S=u((()=>"auto"===b.value?w.value:b.value)),k=C("updateHTMLAttrs",((e,t,n)=>{const r="string"==typeof e?null==i?void 0:i.document.querySelector(e):p(e);if(!r)return;let a;if(v){a=i.document.createElement("style");const e="*,*::before,*::after{-webkit-transition:none!important;-moz-transition:none!important;-o-transition:none!important;-ms-transition:none!important;transition:none!important}";a.appendChild(document.createTextNode(e)),i.document.head.appendChild(a)}if("class"===t){const e=n.split(/\s/g);Object.values(g).flatMap((e=>(e||"").split(/\s/g))).filter(Boolean).forEach((t=>{e.includes(t)?r.classList.add(t):r.classList.remove(t)}))}else r.setAttribute(t,n);v&&(i.getComputedStyle(a).opacity,document.head.removeChild(a))}));function A(e){var t;k(r,a,null!=(t=g[e])?t:e)}function E(e){n.onChanged?n.onChanged(e,A):A(e)}l(S,E,{flush:"post",immediate:!0}),t((()=>E(S.value)));const L=u({get:()=>m?b.value:S.value,set(e){b.value=e}});try{return Object.assign(L,{store:b,system:w,state:S})}catch(j){return L}}({...n,onChanged:(e,t)=>{var r;n.onChanged?null==(r=n.onChanged)||r.call(n,"dark"===e,t,e):t(e)},modes:{dark:r,light:a}}),s=u((()=>{if(i.system)return i.system.value;return N({window:o}).value?"dark":"light"}));return u({get:()=>"dark"===i.value,set(e){const t=e?"dark":"light";s.value===t?i.value="auto":i.value=t}})}export{j as u};
