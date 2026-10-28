/*
 * Highlights the searched keywords in the helpdesk search results.
 * Every element carrying a data-helpdesk-highlight attribute has the words of that attribute
 * wrapped in <mark> elements, ignoring case and accents. Only text nodes are touched, so the
 * HTML of the answers is never broken.
 */
( function( ) {
    'use strict';

    var MIN_TERM_LENGTH = 2;
    var LUCENE_OPERATORS = [ 'and', 'or', 'not' ];
    var SKIPPED_TAGS = [ 'SCRIPT', 'STYLE', 'TEXTAREA', 'MARK', 'NAV' ];

    // Lower case, accents removed: "Élève" -> "eleve"
    function normalize( str ) {
        return str.normalize( 'NFD' ).replace( /[̀-ͯ]/g, '' ).toLowerCase( );
    }

    function escapeRegExp( str ) {
        return str.replace( /[.*+?^${}()|[\]\\]/g, '\\$&' );
    }

    // Splits the query on spaces, drops the Lucene syntax characters and operators
    function getTerms( strKeywords ) {
        var seen = {};
        return strKeywords.split( /\s+/ )
            .map( function( term ) { return normalize( term.replace( /[+\-!(){}[\]^"~*?:\\\/]/g, '' ) ); } )
            .filter( function( term ) {
                if ( term.length < MIN_TERM_LENGTH || LUCENE_OPERATORS.indexOf( term ) !== -1 || seen[term] ) {
                    return false;
                }
                seen[term] = true;
                return true;
            } )
            // Longest first so that a term contained in another one does not cut it
            .sort( function( a, b ) { return b.length - a.length; } );
    }

    // Normalizes a text character by character, keeping for each normalized character its offset in the original text
    function normalizeWithOffsets( text ) {
        var normalized = '';
        var offsets = [];
        for ( var i = 0; i < text.length; i++ ) {
            var chunk = normalize( text.charAt( i ) );
            for ( var j = 0; j < chunk.length; j++ ) {
                normalized += chunk.charAt( j );
                offsets.push( i );
            }
        }
        offsets.push( text.length );
        return { text: normalized, offsets: offsets };
    }

    function highlightTextNode( node, regex ) {
        var text = node.nodeValue;
        var normalized = normalizeWithOffsets( text );
        var fragment = document.createDocumentFragment( );
        var lastIndex = 0;
        var match;

        regex.lastIndex = 0;
        while ( ( match = regex.exec( normalized.text ) ) !== null ) {
            var start = normalized.offsets[match.index];
            var end = normalized.offsets[match.index + match[0].length];
            if ( start > lastIndex ) {
                fragment.appendChild( document.createTextNode( text.substring( lastIndex, start ) ) );
            }
            var mark = document.createElement( 'mark' );
            mark.textContent = text.substring( start, end );
            fragment.appendChild( mark );
            lastIndex = end;
        }

        if ( lastIndex === 0 ) {
            return;
        }
        if ( lastIndex < text.length ) {
            fragment.appendChild( document.createTextNode( text.substring( lastIndex ) ) );
        }
        node.parentNode.replaceChild( fragment, node );
    }

    function highlight( container, terms ) {
        var regex = new RegExp( terms.map( escapeRegExp ).join( '|' ), 'g' );
        var walker = document.createTreeWalker( container, NodeFilter.SHOW_TEXT, {
            acceptNode: function( node ) {
                if ( !node.nodeValue.trim( ) || node.parentElement.closest( SKIPPED_TAGS.join( ',' ) ) ) {
                    return NodeFilter.FILTER_REJECT;
                }
                return NodeFilter.FILTER_ACCEPT;
            }
        } );
        // Collect first: replacing nodes while walking would break the walker
        var nodes = [];
        while ( walker.nextNode( ) ) {
            nodes.push( walker.currentNode );
        }
        nodes.forEach( function( node ) { highlightTextNode( node, regex ); } );
    }

    function init( ) {
        document.querySelectorAll( '[data-helpdesk-highlight]' ).forEach( function( container ) {
            var terms = getTerms( container.getAttribute( 'data-helpdesk-highlight' ) || '' );
            if ( terms.length > 0 ) {
                highlight( container, terms );
            }
        } );
    }

    if ( document.readyState === 'loading' ) {
        document.addEventListener( 'DOMContentLoaded', init );
    } else {
        init( );
    }
}( ) );
