(ns main
  (:require ["@codemirror/language" :refer [HighlightStyle LanguageDescription]]
            ["@lezer/highlight" :refer [tags highlightTree highlightCode]]
            [shadow.esm]))

(def highlight-style
  (.define HighlightStyle
           (clj->js [{:tag (.-meta tags) :class "cmt-meta"}
                     {:tag (.-link tags) :class "cmt-link"}
                     {:tag (.-heading tags) :class "cmt-heading"}
                     {:tag (.-emphasis tags) :class "cmt-italic"}
                     {:tag (.-strong tags) :class "cmt-strong"}
                     {:tag (.-strikethrough tags) :class "cmt-strikethrough"}
                     {:tag (.-keyword tags) :class "cmt-keyword"}
                     {:tag (.-atom tags) :class "cmt-atom"}
                     {:tag (.-bool tags) :class "cmt-bool"}
                     {:tag (.-url tags) :class "cmt-url"}
                     {:tag (.-contentSeparator tags) :class "cmt-contentSeparator"}
                     {:tag (.-labelName tags) :class "cmt-labelName"}
                     {:tag (.-literal tags) :class "cmt-literal"}
                     {:tag (.-inserted tags) :class "cmt-inserted"}
                     {:tag (.-string tags) :class "cmt-string"}
                     {:tag (.-deleted tags) :class "cmt-deleted"}
                     {:tag (.-regexp tags) :class "cmt-regexp"}
                     {:tag (.-escape tags) :class "cmt-escape"}
                     {:tag (.. tags (special (.-string tags))) :class "cmt-string"}
                     {:tag (.. tags (definition (.-variableName tags))) :class "cmt-variableName"}
                     {:tag (.. tags (local (.-variableName tags))) :class "cmt-variableName"}
                     {:tag (.-typeName tags) :class "cmt-typeName"}
                     {:tag (.-namespace tags) :class "cmt-namespace"}
                     {:tag (.-className tags) :class "cmt-className"}
                     {:tag (.. tags (special (.-variableName tags))) :class "cmt-variableName"}
                     {:tag (.-macroName tags) :class "cmt-macroName"}
                     {:tag (.. tags (definition (.-propertyName tags))) :class "cmt-propertyName"}
                     {:tag (.-comment tags) :class "cmt-comment"}
                     {:tag (.-invalid tags) :class "cmt-invalid"}])))

(def code
  #_ "let f = () => {
  let v = 1;
  ++this;
  // comment
  if (true) {
    return 'not evaluated'
  } else {
    return 1234
  }
}" "class Foo(object):
  def init(self, x, y):
    pass
  def do(self, z):
    '''docstring'''
    return 123")

(def language "python" #_ "javascript")

(defn ^:dev/after-load boot []
  (.. (shadow.esm/dynamic-import "/cas/language-data.js")
      #_ (shadow.esm/dynamic-import "https://esm.sh/@codemirror/language-data@6.5.1")
      (then (fn [^js mod]
              (js/console.log :mod mod)
              (when-some [langs (.-languages mod)]
                (when-some [^js matching (or (.matchLanguageName LanguageDescription langs language)
                                             (.matchFilename LanguageDescription langs (str "code." language)))]
                  (.load matching)))))

      (then (fn [^js lang-support]
              (.. lang-support -language -parser)))
      (then
        (fn [parser]
          (js/console.log :parser parser)
          (highlightTree (.parse parser code) highlight-style
                         (fn [from to style] (js/console.log :ht from to style)))
          (highlightCode code (.parse parser code) highlight-style
                         (fn [token class] (js/console.log :hc token class))
                         (fn [] "break"))))))
