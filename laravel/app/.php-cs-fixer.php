<?php

use PhpCsFixer\Config;
use PhpCsFixer\Finder;

$finder = Finder::create()
    ->in([
        __DIR__ . '/app',
        __DIR__ . '/config',
        __DIR__ . '/database',
        __DIR__ . '/routes',
        __DIR__ . '/tests',
    ])
    ->name('*.php')
    ->notName('*.blade.php')
    ->ignoreDotFiles(true)
    ->ignoreVCS(true);

return (new Config())
    ->setRules([
        '@PSR12' => true,
        '@PHP71Migration' => true,

        'declare_strict_types' => true,
        'strict_comparison' => true,
        'strict_param' => true,

        'array_syntax' => ['syntax' => 'short'],
        'ordered_imports' => ['sort_algorithm' => 'alpha'],
        'no_unused_imports' => true,

        'binary_operator_spaces' => [
            'default' => 'single_space',
            'operators' => ['=>' => 'align_single_space_minimal'],
        ],
        'concat_space' => ['spacing' => 'one'],
        'function_typehint_space' => true,
        'method_argument_space' => ['on_multiline' => 'ensure_fully_multiline'],
        'types_spaces' => true,
        'ternary_operator_spaces' => true,
        'blank_line_before_statement' => [
            'statements' => ['break', 'continue', 'declare', 'return', 'throw', 'try'],
        ],

        'blank_line_after_namespace' => true,
        'blank_line_after_opening_tag' => true,
        'no_blank_lines_after_class_opening' => true,
        'no_blank_lines_after_phpdoc' => true,
        'single_blank_line_at_eof' => true,
        'blank_lines_before_namespace' => true,

        'no_extra_blank_lines' => [
            'tokens' => [
                'case', 'continue', 'curly_brace_block', 'default', 'extra',
                'parenthesis_brace_block', 'square_brace_block', 'switch',
                'throw', 'use', 'attribute', 'return',
            ],
        ],
        'no_leading_namespace_whitespace' => true,
        'no_trailing_comma_in_singleline_array' => true,
        'no_whitespace_in_blank_line' => true,
        'no_empty_statement' => true,
        'no_mixed_echo_print' => ['use' => 'echo'],

        'cast_spaces' => true,
        'lowercase_cast' => true,
        'short_scalar_cast' => true,
        'standardize_not_equals' => true,
        'object_operator_without_whitespace' => true,
        'normalize_index_brace' => true,
        'no_spaces_around_offset' => true,
        'no_whitespace_before_comma_in_array' => true,
        'whitespace_after_comma_in_array' => true,

        'phpdoc_align' => ['align' => 'vertical'],
        'phpdoc_indent' => true,
        'phpdoc_no_access' => true,
        'phpdoc_no_package' => true,
        'phpdoc_no_useless_inheritdoc' => true,
        'phpdoc_scalar' => true,
        'phpdoc_summary' => false,
        'phpdoc_trim' => true,
        'phpdoc_types' => true,
        'phpdoc_var_without_name' => true,
        'phpdoc_return_self_reference' => true,
        'phpdoc_separation' => true,
        'phpdoc_single_line_var_spacing' => true,
        'phpdoc_trim_consecutive_blank_line_separation' => true,
        'phpdoc_annotation_without_dot' => true,
        'phpdoc_inline_tag_normalizer' => true,
        'phpdoc_no_alias_tag' => [
            'replacements' => ['type' => 'var', 'linkurl' => 'link'],
        ],
        'php_unit_fqcn_annotation' => true,

        'class_attributes_separation' => [
            'elements' => ['method' => 'one', 'property' => 'one'],
        ],
        'method_chaining_indentation' => true,
        'no_unneeded_curly_braces' => true,
        'no_unneeded_final_method' => true,
        'single_trait_insert_per_statement' => true,
        'self_accessor' => false,
        'yoda_style' => false,

        'single_line_comment_style' => ['comment_types' => ['hash']],
        'multiline_comment_opening_closing' => true,

        'trailing_comma_in_multiline' => ['elements' => ['arrays']],

        'single_quote' => true,

        'return_type_declaration' => ['space_before' => 'none'],
        'semicolon_after_instruction' => true,
        'space_after_semicolon' => ['remove_in_empty_for_expressions' => true],
        'switch_case_semicolon_to_colon' => true,
        'switch_case_space' => true,
        'trim_array_spaces' => true,
    ])
    ->setFinder($finder)
    ->setRiskyAllowed(true)
    ->setUsingCache(true)
;
