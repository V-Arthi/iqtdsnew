import React from 'react'

function HeaderCardField({title,description}) {
    return (
        <div className="field">
            <label>{title}</label>
            <span>{description}</span>
        </div>
    )
}

export default HeaderCardField
