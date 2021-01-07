/* eslint-disable jsx-a11y/anchor-has-content */
/* eslint-disable react/display-name */
import React from 'react';
import Link from 'next/link';

interface IProps extends React.AnchorHTMLAttributes<HTMLAnchorElement> {
  to: string;
  children: React.ReactElement;
  passHref?: boolean;
  prefetch?: boolean;
  className?: string;
}

export default React.forwardRef(({ children, to, passHref = false, prefetch, className, ...props }: IProps, ref: any) => {
  if (passHref) {
    return (
      <Link href={to} passHref prefetch={prefetch || false}>
        {React.cloneElement(children, { ...props, className: className, ref: ref })}
      </Link>
    );
  } else {
    return (
      <Link href={to} prefetch={prefetch || false}>
        <a {...props} className={className} ref={ref}>
          {children}
        </a>
      </Link>
    );
  }
});
