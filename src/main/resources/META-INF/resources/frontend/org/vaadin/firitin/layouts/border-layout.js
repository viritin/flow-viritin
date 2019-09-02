import {PolymerElement, html} from '@polymer/polymer/polymer-element.js';

class BorderLayoutElement extends PolymerElement {
    static get template() {
        return html`
    <style>
      :host {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
      }

      ::slotted(.north) {
        grid-row: 1;
        grid-column: 2;
      }

      ::slotted(.south) {
        grid-row: 3;
        grid-column: 2;
      }

      ::slotted(.east) {
        grid-row: 2;
        grid-column: 3;
      }

      ::slotted(.west) {
        grid-row: 2;
        grid-column: 1;
      }

      ::slotted(.center) {
        grid-row: 2;
        grid-column: 2;
      }
    </style>
    <slot></slot>`;
    }

    static get is() {
        return 'border-layout'
    }
}

customElements.define(BorderLayoutElement.is, BorderLayoutElement);

window.Viritin = window.Viritin || {};

/**
 * @namespace Viritin
 */
window.Viritin.BorderLayoutElement = BorderLayoutElement;
