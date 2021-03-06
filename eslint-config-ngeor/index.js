module.exports = {
    env: {
        es6: true
    },
    extends: [
        'eslint:recommended'
    ],
    'rules': {
        'no-extra-parens': [
            'error'
        ],
        'valid-jsdoc': [
            'error',
            {
                'prefer': {
                    'return': 'returns'
                },
                'preferType': {
                    'Object': 'object',
                    'String': 'string',
                    'Boolean': 'boolean',
                    'Function': 'function'
                },
                'requireReturn': false,
                'requireReturnType': true,
                'requireParamDescription': true,
                'requireReturnDescription': true
            }
        ],
        'block-scoped-var': 'error',
        'complexity': [
            'error',
            4
        ],
        'curly': 'error',
        'default-case': 'error',
        'dot-location': [
            'error',
            'property'
        ],
        'dot-notation': 'error',
        'eqeqeq': 'error',
        'no-eval': 'error',
        'no-magic-numbers': [
            'error',
            {
                'ignore': [
                    0,
                    1
                ]
            }
        ],
        'no-multi-spaces': 'error',
        'no-return-assign': 'error',
        'no-return-await': 'error',
        'no-self-assign': 'error',
        'no-self-compare': 'error',
        'no-throw-literal': 'error',
        'no-unused-expressions': 'error',
        'require-await': 'error',
        'no-shadow': 'error',
        'no-use-before-define': 'error',
        'camelcase': 'error',
        'eol-last': 'error',
        'indent': [
            'error',
            4
        ],
        'max-depth': [
            'error',
            2
        ],
        'max-len': [
            'error',
            100
        ],
        'max-lines': [
            'error',
            200
        ],
        'max-nested-callbacks': [
            'error',
            3
        ],
        'max-params': [
            'error',
            3
        ],
        'max-statements': [
            'error',
            10
        ],
        'max-statements-per-line': 'error',
        'multiline-comment-style': 'error',
        'no-lonely-if': 'error',
        'no-multiple-empty-lines': [
            'error',
            {
                'max': 1,
                'maxEOF': 0,
                'maxBOF': 0
            }
        ],
        'no-negated-condition': 'error',
        'no-tabs': 'error',
        'no-trailing-spaces': 'error',
        'no-whitespace-before-property': 'error',
        'object-curly-newline': [
            'error',
            {
                'ObjectExpression': {
                    'consistent': true,
                    'minProperties': 2
                },
                'ObjectPattern': {
                    'consistent': true,
                    'minProperties': 2
                },
                'ImportDeclaration': 'never',
                'ExportDeclaration': {
                    'consistent': true,
                    'minProperties': 2
                }
            }
        ],
        'object-curly-spacing': [
            'error',
            'always'
        ],
        'object-property-newline': 'error',
        'padded-blocks': [
            'error',
            'never'
        ],
        'quotes': [
            'error',
            'single'
        ],
        'require-jsdoc': [
            'error',
            {
                'require': {
                    'FunctionDeclaration': true,
                    'MethodDefinition': false,
                    'ClassDeclaration': true,
                    'ArrowFunctionExpression': false,
                    'FunctionExpression': true
                }
            }
        ],
        'semi': [
            'error',
            'always'
        ],
        'space-before-blocks': 'error',
        'space-before-function-paren': [
            'error',
            'never'
        ],
        'space-in-parens': 'error',
        'spaced-comment': 'error',
        'unicode-bom': 'error',
        'no-var': 'error',
        'object-shorthand': 'error',
        'prefer-const': 'error'
    }
};
