import React from "react";

const ProgressBar = ({ exp }) => {
  return (
    <div className="bg-neutral-100 w-full h-[8px] rounded-md overflow-hidden my-2">
      <div
        className="bg-purple-500 h-full rounded-md"
        style={{ width: exp + "%", transition: "width 300ms ease" }}
      />
    </div>
  );
};

export default ProgressBar;
