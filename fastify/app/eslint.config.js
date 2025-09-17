import js from '@eslint/js';
import globals from 'globals';
import importPlugin from 'eslint-plugin-import';
import nPlugin from 'eslint-plugin-n';
import security from 'eslint-plugin-security';
import sonarjs from 'eslint-plugin-sonarjs';
import jest from 'eslint-plugin-jest';
import promise from 'eslint-plugin-promise';
import unicorn from 'eslint-plugin-unicorn';
import prettier from 'eslint-plugin-prettier';
import prettierConfig from 'eslint-config-prettier';

export default [
  js.configs.recommended,
  prettierConfig,
  {
    files: ['**/*.{js,ts}'],
    ignores: ['node_modules/**'],
    languageOptions: {
      ecmaVersion: 2025,
      sourceType: 'module',
      globals: {
        ...globals.node,
        ...globals.jest,
      },
    },
    plugins: {
      import: importPlugin,
      node: nPlugin,
      security,
      sonarjs,
      jest,
      promise,
      unicorn,
      prettier,
    },
    rules: {
      'no-console': 'warn',
      'no-debugger': 'error',
      'no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
      eqeqeq: ['error', 'always'],
      curly: ['error', 'all'],
      'prefer-const': 'error',
      'no-var': 'error',
      'import/order': [
        'error',
        {
          groups: [
            ['builtin', 'external'],
            'internal',
            'parent',
            'sibling',
            'index',
          ],
        },
      ],
      'import/no-extraneous-dependencies': 'error',
      'import/prefer-default-export': 'off',
      'node/no-unsupported-features/es-syntax': [
        'error',
        { version: '>=24.5.0' },
      ],
      'node/prefer-global/process': 'error',
      'node/no-process-env': 'warn',
      'security/detect-object-injection': 'error',
      'security/detect-non-literal-regexp': 'error',
      'sonarjs/cognitive-complexity': ['error', 15],
      'sonarjs/no-duplicate-string': 'error',
      'unicorn/prefer-module': 'error',
      'unicorn/no-null': 'error',
      'unicorn/prefer-top-level-await': 'error',
      'promise/always-return': 'error',
      'promise/no-return-wrap': 'error',
      'promise/prefer-await-to-then': 'error',
      'jest/no-disabled-tests': 'warn',
      'jest/valid-expect': 'error',
      'prettier/prettier': [
        'error',
        { singleQuote: true, trailingComma: 'es5', semi: true },
      ],
      'prefer-arrow-callback': 'error',
      'arrow-body-style': ['error', 'as-needed'],
      'no-restricted-syntax': [
        'error',
        { selector: 'ForInStatement', message: 'Use Object.keys instead' },
      ],
    },
  },
  {
    files: ['**/*.test.{js,ts}'],
    rules: {
      'no-console': 'off',
      'node/no-process-env': 'off',
    },
  },
];
